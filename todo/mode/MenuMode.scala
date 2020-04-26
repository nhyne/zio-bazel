package dev.nhyne.todo.mode

import dev.nhyne.todo.domain.TodoList
import zio.ZIO

trait MenuMode {
  val menuMode: MenuMode.Service[Any]
}

object MenuMode {
  trait Service[R] {
    def process(input: String, todoList: TodoList): ZIO[R, Nothing, TodoList]
  }

  object > extends MenuMode.Service[MenuMode] {
    def process(input: String, todoList: TodoList) = {
      ZIO.accessM(_.menuMode.process(input, todoList))
    }
  }
}
