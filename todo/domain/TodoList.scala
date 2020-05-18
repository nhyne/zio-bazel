package dev.nhyne.todo.domain

case class TodoList(
    name: String,
    tasks: Seq[Task]
) {

  final def addTask(task: Task): TodoList = {
    TodoList(tasks = tasks.+:(task), name = name)
  }

}

object TodoList {
  def default(): TodoList = TodoList(tasks = Seq.empty[Task], name = "default")
}
