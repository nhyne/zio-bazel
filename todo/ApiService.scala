package dev.nhyne.todo


import dev.nhyne.todo.persistence.TodoItemPersistenceService
import zio.RIO

import io.circe.Decoder


final case class ApiService[R <: TodoItemPersistenceService](rootUri: String) {
    type TodoTask[A] = RIO[R, A]
}
