package dev.nhyne.todo

import zio.console.putStrLn
import zio.{App, RIO, ZEnv, ZIO}

import cats.effect.ExitCode
import dev.nhyne.todo.configuration.Configuration
import dev.nhyne.todo.configuration.Configuration.Configuration
import dev.nhyne.todo.persistence.TodoItemPersistenceService
import dev.nhyne.todo.persistence.TodoItemPersistenceService.TaskPersistence
import zio.clock.Clock
import org.http4s.server.Router
import org.http4s.server.middleware.CORS
import org.http4s.implicits._
import zio.interop.catz._
import org.http4s.server.blaze.BlazeServerBuilder
import zio.blocking.Blocking

object Main extends App {

  val todoPersistence = (Configuration.live ++ Blocking.live) >>> TodoItemPersistenceService.live
  type ProgramEnv = Configuration with Clock with TaskPersistence

  type AppTask[A] = RIO[ProgramEnv, A]

  def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = {
    val program: ZIO[ProgramEnv, Throwable, Unit] = for {
      config <- Configuration.load
      httpApp = Router[AppTask](
        "/todos" -> ApiService(s"${config.api.endpoint}/todos").route
      ).orNotFound
      server <- ZIO
        .runtime[ProgramEnv]
        .flatMap(implicit rts =>
          BlazeServerBuilder[AppTask]
            .bindHttp(config.api.port, config.api.endpoint)
            .withHttpApp(CORS(httpApp))
            .serve
            .compile[AppTask, AppTask, ExitCode]
            .drain
        )
    } yield server

    program
      .provideSomeLayer[ZEnv](Configuration.live ++ todoPersistence)
      .tapError(err => putStrLn(s"Execution failed with: $err"))
      .fold(_ => 1, _ => 0)
  }

}
