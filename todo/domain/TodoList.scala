package dev.nhyne.todo.domain

case class UninsertedTodoList(
    name: String
)

case class TodoList(
    id: Int,
    name: String
//    todoItems: Seq[TodoItem]
)

case class TodoListNotFound(id: Int) extends Throwable
