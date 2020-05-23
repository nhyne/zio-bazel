package dev.nhyne.todo.persistence

import cats.effect.Blocker
import dev.nhyne.todo.configuration.Configuration.Configuration
import dev.nhyne.todo.configuration.{Configuration, DbConfig}
import doobie.{Query0, Transactor, Update0}
import zio._
import doobie.implicits._
import doobie.postgres.pgisimplicits._
import doobie.hikari._
import zio.interop.catz._
import dev.nhyne.todo.domain.{TodoItem, TodoItemNotFound, UninsertedTodoItem}
import zio.blocking.Blocking

import scala.concurrent.ExecutionContext

final class TodoItemPersistenceService(tnx: Transactor[Task])
    extends Persistence.Service[TodoItem, UninsertedTodoItem] {
  import TodoItemPersistenceService._

  override def get(id: Int): Task[TodoItem] =
    SQL
      .get(id)
      .option
      .transact(tnx)
      .foldM(
        err => Task.fail(err),
        maybeTodoItem =>
          Task.require(TodoItemNotFound(id))(Task.succeed(maybeTodoItem))
      )

  override def create(todo: UninsertedTodoItem): Task[TodoItem] =
    SQL
      .create(todo)
      .withUniqueGeneratedKeys[TodoItem](
        "id",
        "title",
        "description",
        "completed",
        "list_id"
      )
      .transact(tnx)
      .foldM(
        err => {
          println(s"Error: $err")
          Task.fail(err)
        },
        todo => Task.succeed(todo)
      )

  override def delete(id: Int): Task[Boolean] =
    SQL
      .delete(id)
      .run
      .transact(tnx)
      .fold(
        _ => false,
        _ => true
      )
}

object TodoItemPersistenceService {

  type TaskPersistence = Has[Persistence.Service[TodoItem, UninsertedTodoItem]]

  val live: ZLayer[
    Configuration with Blocking,
    Throwable,
    TaskPersistence
  ] =
    ZLayer.fromManaged(
      for {
        config <- Configuration.load.toManaged_
        connectEC <- ZIO.descriptor.map(_.executor.asEC).toManaged_
        managed <- mkTransactor(config.dbConfig, connectEC)
      } yield managed
    )

  def getTodoItem(id: Int): RIO[TaskPersistence, TodoItem] =
    RIO.accessM[TaskPersistence](_.get.get(id))

  def createTodoItem(task: UninsertedTodoItem): RIO[TaskPersistence, TodoItem] =
    RIO.accessM[TaskPersistence](_.get.create(task))

  def deleteTodoItem(id: Int): RIO[TaskPersistence, Boolean] =
    RIO.accessM[TaskPersistence](_.get.delete(id))

  object SQL {
    def get(id: Int): Query0[TodoItem] =
      sql"""SELECT * FROM TASKS WHERE ID = $id""".query[TodoItem]

    def create(task: UninsertedTodoItem): Update0 =
      sql"""INSERT INTO TASKS (title, description, list_id) VALUES (${task.title}, ${task.description}, 2)""".update

    def delete(id: Int): Update0 =
      sql"""DELETE FROM TASKS WHERE id = $id""".update
  }

  def mkTransactor(
      conf: DbConfig,
      connectEC: ExecutionContext
  ): Managed[Throwable, TodoItemPersistenceService] = {
    val transactor =
      for {
        blocker <- Blocker[Task]
        tnxr <- HikariTransactor.newHikariTransactor[Task](
          driverClassName = "org.postgresql.Driver",
          url = conf.url,
          user = conf.user,
          pass = conf.password,
          connectEC = connectEC,
          blocker = blocker
        )
      } yield tnxr

    transactor.toManagedZIO
      .map(new TodoItemPersistenceService(_))
  }

}
