package dev.nhyne.todo.persistence

import dev.nhyne.todo.domain.TodoItem
import zio.Task

object Persistence {

  trait Service[A, B] {
    def get(id: Int): Task[A]
    def create(todo: B): Task[A]
    def delete(id: Int): Task[Boolean]
  }
}
