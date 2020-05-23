package dev.nhyne.todo.domain

final case class TodoItem(
    id: Int,
    title: String,
    description: Option[String],
    completed: Boolean = false,
    listId: Int
) {

  final def complete(): TodoItem =
    TodoItem(
      id = id,
      title = title,
      description = description,
      completed = true,
      listId = listId
    )
}

final case class TodoItemNotFound(id: Int) extends Exception
