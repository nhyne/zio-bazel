package dev.nhyne.todo

import cats.data.Kleisli
import cats.effect.Blocker
import org.http4s.StaticFile
import zio.console.putStrLn
import zio.{App, RIO, ZEnv, ZIO}
//import cats.effect.ExitCode
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

  type AppTask[A] = RIO[ProgramEnv, A]

  def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = {
    val program: ZIO[ProgramEnv, Throwable, Unit] = for {
      blocker <- ZIO
        .access[Blocking](_.get.blockingExecutor.asEC)
        .map(Blocker.liftExecutionContext)
      config <- Configuration.load
      api = GraphqlService.api
      interpreter <- api.interpreter
      httpApp: Kleisli[AppTask, Request[AppTask], Response[AppTask]] = Router[
        AppTask
      ](
        "/api/graphql" -> Http4sAdapter.makeHttpService(interpreter),
        "/ws/graphql" -> Http4sAdapter.makeWebSocketService(interpreter),
        "/graphiql" -> Kleisli.liftF(
          StaticFile.fromResource("graphiql.html", blocker, None)
        )
      ).orNotFound
      server <- ZIO
        .runtime[ProgramEnv]
        .flatMap(implicit rts =>
          BlazeServerBuilder[AppTask]
            .bindHttp(config.api.port, config.api.endpoint)
            .withHttpApp(httpApp)
            .resource
            .toManagedZIO
            .useForever
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
