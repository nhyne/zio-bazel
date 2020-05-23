package dev.nhyne.todo.persistence

import dev.nhyne.todo.domain.TodoItem
import dev.nhyne.todo.persistence.TodoItemPersistenceService.TaskPersistence
import zio.{RIO, Task}

object Persistence {

  trait Service[A] {
    def get(id: Int): Task[A]
    def create(todo: TodoItem): Task[A]
    def delete(id: Int): Task[Boolean]
  }
    def getTodoItem(id: Int): RIO[TaskPersistence, TodoItem] = RIO.accessM[TaskPersistence](_.get.get(id))
}
