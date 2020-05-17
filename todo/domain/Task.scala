package dev.nhyne.todo.domain

case class Task(
    title: String,
    description: String,
    completed: Boolean = false,
    listId: Int
) {

  final def complete(): Task =
    Task(
      title = title,
      description = description,
      completed = true,
      listId = listId
    )
}
