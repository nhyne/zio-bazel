package dev.nhyne.todo

import zio.{Chunk, Has, Task, ZIO, ZLayer}
import zio.query.{CompletedRequestMap, DataSource, Request, ZQuery}
import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import doobie.postgres.pgisimplicits._
import cats._
import cats.effect._
import cats.implicits._
import zio.console.{Console, putStrLn}

import java.io.IOException

object PostgresConnection {

  trait Service {
    def printVal(): ZIO[PostgresConnection, IOException, Unit]
  }

  type PostgresConnection = Has[Service] with Console

  val live = ZLayer.succeed(new Service {
    override def printVal(): ZIO[PostgresConnection, IOException, Unit] = {
      val program1 = sql"select name from todo_lists".query[String].unique
      implicit val cs = IO.contextShift(ExecutionContexts.synchronous)
      val xa = Transactor.fromDriverManager[IO](
        "org.postgresql.Driver",
        "jdbc:postgresql:todo",
        "todo",
        "password",
        Blocker.liftExecutionContext(ExecutionContexts.synchronous)
      )
      val io = program1.transact(xa)
      val something = io.unsafeRunSync

      for {
        _ <- putStrLn(s"$something")
      } yield ()
    }
  })

  def printVal(): ZIO[PostgresConnection, IOException, Unit] = {
    ZIO.accessM[PostgresConnection](_.get.printVal())
  }

  case class GetTodoItem(id: Int) extends Request[Nothing, String]

  lazy val TodoItemDataSource = new DataSource.Batched[Any, GetTodoItem] {
    override val identifier: String = "TodoItemDataSource"
    def run(
        requests: Chunk[GetTodoItem]
    ): ZIO[Any, Nothing, CompletedRequestMap] = ???
  }

  def getTodoItemById(id: Int): ZQuery[Any, Nothing, String] =
    ZQuery.fromRequest(GetTodoItem(id))(TodoItemDataSource)

  def run(
      requests: Chunk[GetTodoItem]
  ): ZIO[Any, Nothing, CompletedRequestMap] = {
    val resultMap = CompletedRequestMap.empty
    requests.toList match {
      case request :: Nil =>
        val result: Task[String] = ???
        result.either.map(resultMap.insert(request))
      case batch =>
        val result: Task[List[(Int, String)]] = ???
        result.fold(
          err =>
            requests.foldLeft(resultMap) {
              case (map, req) => map.insert(req)(Left(err))
            },
          _.foldLeft(resultMap) {
            case (map, (id, name)) => map.insert(GetTodoItem(id))(Right(name))
          }
        )
    }
  }
}
