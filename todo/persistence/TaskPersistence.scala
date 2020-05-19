package dev.nhyne.todo.persistence

import doobie.{Query0, Transactor, Update0}
import zio._
import doobie.implicits._
import zio.interop.catz._
import dev.nhyne.todo.domain.{TodoItem, TodoItemNotFound}

final class TaskPersistence(tnx: Transactor[Task])
    extends Persistence.Service[TodoItem] {
  import TaskPersistence._

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

object TaskPersistence {
  object SQL {
    def get(id: Int): Query0[TodoItem] =
      sql"""SELECT * FROM TASKS WHERE ID = $id""".query[TodoItem]

    def create(task: TodoItem): Update0 =
      sql"""INSERT INTO USERS (title, list_id) VALUES (${task.title}, 2)""".update

    def delete(id: Int): Update0 =
      sql"""DELETE FROM TASKS WHERE id = $id""".update
  }

}
