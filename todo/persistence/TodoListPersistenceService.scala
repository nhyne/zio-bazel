package dev.nhyne.todo.persistence

import dev.nhyne.todo.configuration.Configuration.Configuration
import dev.nhyne.todo.configuration.{Configuration, DbConfig}
import dev.nhyne.todo.domain.{
  TodoItem,
  TodoList,
  TodoListNotFound,
  UninsertedTodoList
}
import doobie.{Query0, Transactor, Update0}
import doobie.implicits._
import doobie.util.Read
import zio.blocking.Blocking
import zio.interop.catz._
import zio.{Has, Managed, RIO, Task, ZIO, ZLayer}

import scala.concurrent.ExecutionContext

final case class TodoListPersistenceService(tnx: Transactor[Task])
    extends Persistence.Service[TodoList, UninsertedTodoList] {

  import TodoListPersistenceService._
  override def get(id: Int): Task[TodoList] =
    SQL
      .get(id)
      .option
      .transact(tnx)
      .foldM(
        err => Task.fail(err),
        maybeTodoList =>
          Task.require(TodoListNotFound(id))(Task.succeed(maybeTodoList))
      )

  override def create(todo: UninsertedTodoList): Task[TodoList] =
    SQL
      .create(todo)
      .withUniqueGeneratedKeys[TodoList]("name")
      .transact(tnx)
      .foldM(
        err => Task.fail(err),
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

object TodoListPersistenceService {
  type TodoPersistence = Has[Persistence.Service[TodoList, UninsertedTodoList]]

  // TODO: Need to actually get relational data in here
  implicit val todoItemRead: Read[TodoList] =
    Read[(Int, String)].map {
      case (id, name) => TodoList(id, name, Seq.empty[TodoItem])
    }

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

  def getTodoList(id: Int): RIO[TodoPersistence, TodoList] =
    RIO.accessM[TodoPersistence](_.get.get(id))

  def deleteTodoList(id: Int): RIO[TodoPersistence, Boolean] =
    RIO.accessM[TodoPersistence](_.get.delete(id))

  def createTodoList(
      todoList: UninsertedTodoList
  ): RIO[TodoPersistence, TodoList] =
    RIO.accessM[TodoPersistence](_.get.create(todoList))

  object SQL {
    def get(id: Int): Query0[TodoList] =
      sql"""SELECT * FROM TODO_LISTS WHERE ID = $id""".query[TodoList]

    def create(todoList: UninsertedTodoList): Update0 =
      sql"""INSERT INTO TODO_LISTS (name) VALUES (${todoList.name})""".update

    def delete(id: Int): Update0 =
      sql"""DELETE FROM TODO_LISTS WHERE id = $id""".update
  }

  def mkTransactor(
      conf: DbConfig,
      connectEC: ExecutionContext
  ): Managed[Throwable, TodoListPersistenceService] = {
    mkBaseTransactor(conf, connectEC).toManagedZIO
      .map(new TodoListPersistenceService(_))
  }

}
