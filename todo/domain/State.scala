package dev.nhyne.todo.domain

sealed trait State

object State {
  final case class NewTask(list: TodoList, task: Task) extends State

  final case class Menu(list: TodoList) extends State

  case object Shutdown extends State

  def default(): State = State.Menu(TodoList.default())
}
