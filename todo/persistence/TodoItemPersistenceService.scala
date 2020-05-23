package dev.nhyne.todo.persistence

import cats.effect.Blocker
import dev.nhyne.todo.configuration.Configuration.Configuration
import dev.nhyne.todo.configuration.{Configuration, DbConfig}
import doobie.{Query0, Transactor, Update0}
import zio._
import doobie.implicits._
import doobie.hikari._
import zio.interop.catz._
import dev.nhyne.todo.domain.{TodoItem, TodoItemNotFound}
import zio.blocking.Blocking

import scala.concurrent.ExecutionContext

final class TodoItemPersistenceService(tnx: Transactor[Task])
    extends Persistence.Service[TodoItem] {
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

  override def create(todo: TodoItem): Task[TodoItem] =
    SQL
      .create(todo)
      .run
      .transact(tnx)
      .foldM(
        err => Task.fail(err),
        _ => Task.succeed(todo)
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

  type TaskPersistence = Has[Persistence.Service[TodoItem]]

  val live: ZLayer[Configuration with Blocking, Throwable, TaskPersistence] =
    ZLayer.fromManaged(
      for {
        config <- Configuration.load.toManaged_
        connectEC <- ZIO.descriptor.map(_.executor.asEC).toManaged_
        managed <- mkTransactor(config.dbConfig, connectEC)
      } yield managed
    )

  def getTodoItem(id: Int): RIO[TaskPersistence, TodoItem] =
    RIO.accessM[TaskPersistence](_.get.get(id))

  object SQL {
    def get(id: Int): Query0[TodoItem] =
      sql"""SELECT * FROM TASKS WHERE ID = $id""".query[TodoItem]

    def create(task: TodoItem): Update0 =
      sql"""INSERT INTO USERS (title, list_id) VALUES (${task.title}, 2)""".update

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
