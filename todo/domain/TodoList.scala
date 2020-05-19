package dev.nhyne.todo.domain

case class TodoList(
    name: String,
    tasks: Seq[TodoItem]
) {

  final def addTask(task: TodoItem): TodoList = {
    TodoList(tasks = tasks.+:(task), name = name)
  }

}

object TodoList {
  def default(): TodoList =
    TodoList(tasks = Seq.empty[TodoItem], name = "default")
}
