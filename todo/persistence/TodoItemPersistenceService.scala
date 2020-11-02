package dev.nhyne.todo.persistence

import dev.nhyne.todo.configuration.Configuration.Configuration
import dev.nhyne.todo.configuration.{Configuration, DbConfig}
import doobie.{Query0, Transactor, Update0}
import zio._
import doobie.implicits._
import zio.interop.catz._
import zio.blocking.Blocking

import scala.concurrent.ExecutionContext

final case class UninsertedTodoItem(
  title: String,
  description: Option[String],
  completed: Boolean = false,
  listId: Int
)

final case class TodoItem(
  id: Int,
  title: String,
  description: Option[String],
  listId: Int,
  completed: Boolean = false
) {

  final def complete(): TodoItem =
    TodoItem(
      id = id,
      title = title,
      description = description,
      completed = true,
      listId = listId
    )
}

final case class TodoItemNotFound(id: Int) extends Exception

final class TodoItemPersistenceService(tnx: Transactor[Task])
    extends TodoItemPersistenceService.Service {
  import TodoItemPersistenceService._

  def get(id: Int): Task[TodoItem] =
    SQL
      .get(id)
      .option
      .transact(tnx)
      .foldM(
        err => Task.fail(err),
        maybeTodoItem =>
          Task.require(TodoItemNotFound(id))(Task.succeed(maybeTodoItem))
      )

  override def getTodosForList(
    listId: Int
  ): Task[List[TodoItem]] =
    SQL
      .getTodosForListId(listId)
      .to[List]
      .transact(tnx)
      .foldM(
        err => {
          println(s"Error: $err")
          Task.fail(err)
        },
        a => Task.succeed(a)
      )

  override def markComplete(
    id: Int
  ): Task[TodoItem] =
    SQL
      .markComplete(id)
      .run
      .transact(tnx)
      .foldM(err => Task.fail(err), _ => get(id))

  def create(todo: UninsertedTodoItem): Task[TodoItem] =
    SQL
      .create(todo)
      .withUniqueGeneratedKeys[TodoItem](
        "id",
        "title",
        "description",
        "list_id",
        "completed"
      )
      .transact(tnx)
      .foldM(
        err => {
          println(s"Error: $err")
          Task.fail(err)
        },
        todo => Task.succeed(todo)
      )

  def delete(id: Int): Task[Boolean] =
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

  trait Service {
    def get(id: Int): ZIO[TaskPersistence, Throwable, TodoItem]
    def getTodosForList(
      listId: Int
    ): ZIO[TaskPersistence, Throwable, List[TodoItem]]
    def create(
      todo: UninsertedTodoItem
    ): ZIO[TaskPersistence, Throwable, TodoItem]
    def markComplete(id: Int): ZIO[TaskPersistence, Throwable, TodoItem]
    def delete(id: Int): ZIO[TaskPersistence, Throwable, Boolean]
  }

  type TaskPersistence = Has[Service]

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

  def getTodosForList(listId: Int): RIO[TaskPersistence, List[TodoItem]] =
    RIO.accessM[TaskPersistence](_.get.getTodosForList(listId))

  def createTodoItem(task: UninsertedTodoItem): RIO[TaskPersistence, TodoItem] =
    RIO.accessM[TaskPersistence](_.get.create(task))

  def markTodoItemComplete(id: Int): RIO[TaskPersistence, TodoItem] =
    RIO.accessM[TaskPersistence](_.get.markComplete(id))

  def deleteTodoItem(id: Int): RIO[TaskPersistence, Boolean] =
    RIO.accessM[TaskPersistence](_.get.delete(id))

  object SQL {
    def get(id: Int): Query0[TodoItem] =
      sql"""SELECT * FROM TODO_ITEMS WHERE ID = $id""".query[TodoItem]

    def getTodosForListId(listId: Int): Query0[TodoItem] =
      sql"""SELECT * FROM TODO_ITEMS WHERE LIST_ID = $listId"""
        .query[TodoItem]
    def markComplete(id: Int): Update0 =
      sql"""UPDATE TODO_ITEMS SET completed = true WHERE id = $id""".update

    def create(task: UninsertedTodoItem): Update0 =
      sql"""INSERT INTO TODO_ITEMS (title, description, list_id) VALUES (${task.title}, ${task.description}, 2)""".update

    def delete(id: Int): Update0 =
      sql"""DELETE FROM TODO_ITEMS WHERE id = $id""".update
  }

  def mkTransactor(
    conf: DbConfig,
    connectEC: ExecutionContext
  ): Managed[Throwable, TodoItemPersistenceService] =
    mkBaseTransactor(conf, connectEC).toManagedZIO
      .map(new TodoItemPersistenceService(_))

}
