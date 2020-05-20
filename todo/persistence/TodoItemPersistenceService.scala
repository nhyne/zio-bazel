package dev.nhyne.todo.persistence

import dev.nhyne.todo.configuration.Configuration.Configuration
import dev.nhyne.todo.configuration.{Configuration, DbConfig}
import doobie.{Query0, Transactor, Update0}
import zio._
import doobie.implicits._
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
                blockingEc <- ZIO.access[Blocking](_.get.blockingExecutor.asEC).toManaged_
                managed <- mkTransactor(config.dbConfig, connectEC, blockingEc)
            } yield managed
        )

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
      connectEC: ExecutionContext,
      transactEC: ExecutionContext
  ): Managed[Throwable, TodoItemPersistenceService] = {
    val managedTransactor = Managed.fromEffect(
      // TODO: This does not have a release action but needs one
        // Also look at other options for the Transactor including this: https://github.com/tpolecat/doobie/blob/0ce7a2eb345ac6c38addbe46ad3c320d15167937/modules/example/src/main/scala/example/HikariExample.scala
      Task.apply(
        Transactor.fromDriverManager[Task](
          driver = "org.postgresql.Driver",
          url = conf.url,
          user = conf.user,
          pass = conf.password
        )
      )
    )
    managedTransactor.map(new TodoItemPersistenceService(_))
  }

}
