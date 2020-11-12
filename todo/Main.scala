package dev.nhyne.todo

import cats.data.Kleisli
import cats.effect.Blocker
import cats.effect.{ExitCode => CatsExitCode}
import dev.nhyne.todo.persistence.{
  NotePersistenceService,
  TodoItemPersistenceService,
  TodoListPersistenceService
}
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, StaticFile}
import zio.console.putStrLn
import zio._
import dev.nhyne.todo.configuration.Configuration
import dev.nhyne.todo.configuration.Configuration.Configuration
import org.http4s.{Request, Response}
import org.http4s.server.Router
import org.http4s.implicits._
import org.http4s.server.middleware.CORS
import caliban.Http4sAdapter
import zio.interop.catz._
import org.http4s.server.blaze.BlazeServerBuilder
import zio.blocking.Blocking

object Main extends App {

  val todoPersistence =
    (Configuration.live ++ Blocking.live) >>> TodoItemPersistenceService.live
  val todoListPersistence =
    (Configuration.live ++ Blocking.live) >>> TodoListPersistenceService.live
  val notePersistence =
    (Configuration.live ++ Blocking.live) >>> NotePersistenceService.live
  type ProgramEnv = Configuration with Env with ZEnv

  type AppTask[A] = RIO[ProgramEnv, A]
  object http4sDsl extends Http4sDsl[AppTask]

  def run(args: List[String]): URIO[ZEnv, ExitCode] = {
    import http4sDsl._
    val program: ZIO[ProgramEnv, Throwable, Unit] = for {
      blocker <-
        ZIO
          .access[Blocking](_.get.blockingExecutor.asEC)
          .map(Blocker.liftExecutionContext)
      config <- Configuration.load
      api = GraphqlService.api
      interpreter <- api.interpreter
      httpApp: Kleisli[AppTask, Request[AppTask], Response[AppTask]] =
        Router[
          AppTask
        ](
          "/api/graphql" -> Http4sAdapter.makeHttpService(interpreter),
          "/ws/graphql" -> Http4sAdapter.makeWebSocketService(interpreter),
          "/graphiql" -> Kleisli.liftF(
            StaticFile.fromResource("graphiql.html", blocker, None)
          ),
          "/schema" -> HttpRoutes.of {
            case request if request.method == org.http4s.Method.GET =>
              Ok(api.render)
          }
        ).orNotFound
      server <-
        ZIO
          .runtime[ProgramEnv]
          .flatMap(implicit rts =>
            BlazeServerBuilder[AppTask]
              .bindHttp(config.api.port, config.api.endpoint)
              .withHttpApp(CORS(httpApp))
              .resource
              .toManagedZIO
              .useForever
          )
    } yield server

    program
      .provideSomeLayer[ZEnv](
        Configuration.live ++ todoPersistence ++ todoListPersistence ++ notePersistence
      )
      .tapError(err => putStrLn(s"Execution failed with: $err"))
      .fold(_ => ExitCode(1), _ => ExitCode(0))
  }

}
