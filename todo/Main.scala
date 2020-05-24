package dev.nhyne.todo

import cats.data.Kleisli
import zio.console.putStrLn
import zio.{App, RIO, ZEnv, ZIO}
import cats.effect.ExitCode
import dev.nhyne.todo.configuration.Configuration
import dev.nhyne.todo.configuration.Configuration.Configuration
import dev.nhyne.todo.persistence.TodoItemPersistenceService
import dev.nhyne.todo.persistence.TodoItemPersistenceService.TaskPersistence
import org.http4s.{Response, Request}
import org.http4s.server.Router
import org.http4s.implicits._

//import scala.concurrent.ExecutionContext
//import org.http4s.server.middleware.CORS
import caliban.Http4sAdapter
import zio.interop.catz._
import org.http4s.server.blaze.BlazeServerBuilder
import zio.blocking.Blocking
//import zio.logging._

//import scala.concurrent._
//import ExecutionContext.Implicits.global

object Main extends App {

//  val defaultLogger = Logging.console(
//    format = (_, logEntry) => logEntry,
//    rootLoggerName = Some("default-logger")
//  )
  val todoPersistence = (Configuration.live ++ Blocking.live) >>> TodoItemPersistenceService.live
  type ProgramEnv = Configuration
    with TaskPersistence
//    with Logging
    with ZEnv

  val query =
    """
          |{
          |  getTodo(id: 1) {
          |    title
          |    description
          |  }
          |}
          |""".stripMargin

  type AppTask[A] = RIO[ProgramEnv, A]

  def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = {
    val program: ZIO[ProgramEnv, Throwable, Unit] = for {
//        blocker <- ZIO.access[Blocking](_.get.blockingExecutor.asEC)
      config <- Configuration.load
      api = GraphqlService.api
      interpreter <- api.interpreter
      result <- interpreter.execute(query)
      _ <- putStrLn(result.data.toString)
      httpApp: Kleisli[AppTask, Request[AppTask], Response[AppTask]] = Router[
        AppTask
      ](
        "/graphql" -> Http4sAdapter.makeHttpService(interpreter)
//        "/todos" -> ApiService(s"${config.api.endpoint}/todos").route
      ).orNotFound
      server <- ZIO
        .runtime[ProgramEnv]
        .flatMap(implicit rts =>
          BlazeServerBuilder[AppTask]
            .bindHttp(config.api.port, config.api.endpoint)
            .withHttpApp(httpApp)
            .serve
            .compile[AppTask, AppTask, ExitCode]
            .drain
        )
    } yield server

    program
      .provideSomeLayer[ZEnv](
        Configuration.live ++ todoPersistence
      )
      .tapError(err => putStrLn(s"Execution failed with: $err"))
      .fold(_ => 1, _ => 0)
  }

}
