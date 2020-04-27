package dev.nhyne.todo.domain

case class TodoList(tasks: Seq[Task]) {

  final def addTask(task: Task): TodoList = {
    TodoList(tasks.+:(task))
  }

}

object TodoList {
  def default(): TodoList = TodoList(Seq.empty)
}
