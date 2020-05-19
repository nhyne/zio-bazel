package dev.nhyne.todo.domain

final case class TodoItem(
    title: String,
    description: String,
    completed: Boolean = false,
    listId: Int
) {

  final def complete(): TodoItem =
    TodoItem(
      title = title,
      description = description,
      completed = true,
      listId = listId
    )
}

final case class TodoItemNotFound(id: Int) extends Exception
