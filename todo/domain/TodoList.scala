package dev.nhyne.todo.domain

case class UninsertedTodoList(
                             name: String
                             )

case class TodoList(
    id: Int,
                       name: String
)

object TodoList {
    def default(): TodoList = TodoList(id = 1, name = "default")
}

case class TodoListNotFound(id: Int) extends Throwable
