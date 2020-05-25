package dev.nhyne.todo.persistence

import dev.nhyne.todo.configuration.Configuration.Configuration
import dev.nhyne.todo.configuration.{Configuration, DbConfig}
import dev.nhyne.todo.persistence.TodoItemPersistenceService.TaskPersistence
import dev.nhyne.todo.persistence.TodoListPersistenceService.TodoPersistence
import doobie.{Query0, Transactor, Update0}
import doobie.implicits._
import zio.blocking.Blocking
import zio.interop.catz._
import zio.{Has, Managed, RIO, Task, ZIO, ZLayer}
import io.scalaland.chimney.dsl._

import scala.concurrent.ExecutionContext

case class UninsertedTodoList(
    name: String
)

case class TodoList(
    id: Int,
    name: String
)

case class CalibanTodoList(
    id: Int,
    name: String,
    todoItems: ZIO[TaskPersistence, Throwable, List[TodoItem]]
)

case class TodoListNotFound(id: Int) extends Throwable

final case class TodoListPersistenceService(tnx: Transactor[Task])
    extends TodoListPersistenceService.Service {
  import TodoListPersistenceService._

  def get(id: Int): Task[CalibanTodoList] =
    SQL
      .get(id)
      .option
      .transact(tnx)
      .foldM(
        err => Task.fail(err),
        maybeTodoList =>
          Task.require(TodoListNotFound(id))(
            ZIO.some(
              maybeTodoList.get
                .into[CalibanTodoList]
                .withFieldComputed(
                  _.todoItems,
                  // This would be where we would query for all TodoItems with the listId
                  x => ZIO.accessM[TaskPersistence](_.get.getTodosForList(x.id))
                )
                .transform
            )
          )
      )

  def create(list: UninsertedTodoList): Task[TodoList] =
    SQL
      .create(list)
      .withUniqueGeneratedKeys[TodoList]("name")
      .transact(tnx)
      .foldM(
        err => Task.fail(err),
        insertedList => Task.succeed(insertedList)
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

  override def getTodoLists(
      limit: Int
  ): ZIO[TodoPersistence, Throwable, List[TodoList]] =
    SQL
      .getTodoLists(limit)
      .to[List]
      .transact(tnx)

}

object TodoListPersistenceService {
  trait Service {
    def get(id: Int): ZIO[TodoPersistence, Throwable, CalibanTodoList]
    def create(
        list: UninsertedTodoList
    ): ZIO[TodoPersistence, Throwable, TodoList]
    def delete(id: Int): ZIO[TodoPersistence, Throwable, Boolean]
    def getTodoLists(
        limit: Int
    ): ZIO[TodoPersistence, Throwable, List[TodoList]]
  }

  type TodoPersistence = Has[Service]

  // TODO: Need to actually get relational data in here
//  implicit val todoItemRead: Read[TodoList] =
//    Read[(Int, String)].map {
//      case (id, name) => TodoList(id, name, Seq.empty[TodoItem])
//    }

  val live: ZLayer[
    Configuration with Blocking,
    Throwable,
    TodoPersistence
  ] = ZLayer.fromManaged(
    for {
      config <- Configuration.load.toManaged_
      connectEC <- ZIO.descriptor.map(_.executor.asEC).toManaged_
      managed <- mkTransactor(config.dbConfig, connectEC)
    } yield managed
  )

  def getTodoList(id: Int): RIO[TodoPersistence, CalibanTodoList] =
    RIO.accessM[TodoPersistence](_.get.get(id))

  def deleteTodoList(id: Int): RIO[TodoPersistence, Boolean] =
    RIO.accessM[TodoPersistence](_.get.delete(id))

  def createTodoList(
      todoList: UninsertedTodoList
  ): RIO[TodoPersistence, TodoList] =
    RIO.accessM[TodoPersistence](_.get.create(todoList))

  def getTodoLists(limit: Int): RIO[TodoPersistence, List[TodoList]] =
    RIO.accessM[TodoPersistence](_.get.getTodoLists(limit))

  object SQL {
    def get(id: Int): Query0[TodoList] =
      sql"""SELECT * FROM TODO_LISTS WHERE ID = $id""".query[TodoList]

    def create(todoList: UninsertedTodoList): Update0 =
      sql"""INSERT INTO TODO_LISTS (name) VALUES (${todoList.name})""".update

    def delete(id: Int): Update0 =
      sql"""DELETE FROM TODO_LISTS WHERE id = $id""".update

    def getTodoLists(limit: Int): Query0[TodoList] =
      sql"""SELECT * FROM TODO_LISTS LIMIT $limit""".query[TodoList]
  }

  def mkTransactor(
      conf: DbConfig,
      connectEC: ExecutionContext
  ): Managed[Throwable, TodoListPersistenceService] = {
    mkBaseTransactor(conf, connectEC).toManagedZIO
      .map(new TodoListPersistenceService(_))
  }

}
