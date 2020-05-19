package dev.nhyne.todo.persistence

import dev.nhyne.todo.domain.TodoItem
import zio.Task

object Persistence {

  trait Service[A] {
    def get(id: Int): Task[A]
    def create(todo: TodoItem): Task[A]
    def delete(id: Int): Task[Boolean]
  }
}
