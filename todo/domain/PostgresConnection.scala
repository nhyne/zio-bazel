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

import dev.nhyne.todo.domain.TodoList

object PostgresConnection {

  trait Service {
    def printVal(): ZIO[PostgresConnection, IOException, Unit]

//      def getList(id: Int): ZIO[PostgresConnection, IOException, TodoList]

    def insertTask(
        task: dev.nhyne.todo.domain.Task
    ): ZIO[PostgresConnection, IOException, Unit]

    def insertTodoList(
        list: TodoList
    ): ZIO[PostgresConnection, IOException, Unit]
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
        "password"
      )
      val io = program1.transact(xa)
      val something = io.unsafeRunSync

      for {
        _ <- putStrLn(s"$something")
      } yield ()
    }

    override def insertTask(
        task: _root_.dev.nhyne.todo.domain.Task
    ): _root_.zio.ZIO[
      _root_.dev.nhyne.todo.PostgresConnection.PostgresConnection,
      _root_.java.io.IOException,
      Unit
    ] = {
      val statement =
        sql"insert into tasks (title, description, completed, list_id) values (${task.title}, ${task.description}, ${task.completed}, ${task.listId})".update
      implicit val cs = IO.contextShift(ExecutionContexts.synchronous)
      val xa = Transactor.fromDriverManager[IO](
        "org.postgresql.Driver",
        "jdbc:postgresql:todo",
        "todo",
        "password"
      )
      val io = statement.run.transact(xa)
      val something = io.unsafeRunSync
      for {
        _ <- putStrLn(s"$something")
      } yield ()

    }

    override def insertTodoList(
        list: _root_.dev.nhyne.todo.domain.TodoList
    ): _root_.zio.ZIO[
      _root_.dev.nhyne.todo.PostgresConnection.PostgresConnection,
      _root_.java.io.IOException,
      Unit
    ] = {
      val statement =
        sql"insert into todo_lists (name) values (${list.name})".update
      implicit val cs = IO.contextShift(ExecutionContexts.synchronous)
      val xa = Transactor.fromDriverManager[IO](
        "org.postgresql.Driver",
        "jdbc:postgresql:todo",
        "todo",
        "password"
      )
      val io = statement.run.transact(xa)
      val something = io.unsafeRunSync
      for {
        _ <- putStrLn(s"$something")
      } yield ()
    }

  })

  def printVal(): ZIO[PostgresConnection, IOException, Unit] = {
    ZIO.accessM[PostgresConnection](_.get.printVal())
  }

  def insertTask(
      task: dev.nhyne.todo.domain.Task
  ): ZIO[PostgresConnection, IOException, Unit] = {
    ZIO.accessM[PostgresConnection](_.get.insertTask(task))
  }

  def insertTodoList(
      list: TodoList
  ): ZIO[PostgresConnection, IOException, Unit] = {
    ZIO.accessM[PostgresConnection](_.get.insertTodoList(list))
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
