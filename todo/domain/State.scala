package dev.nhyne.todo.domain

import java.io.IOException

sealed trait State {
  def getList(): TodoList
}

object State {
  final case class NewTask(list: TodoList, task: Option[Task]) extends State {
    def getList() = list
  }

  final case class Menu(list: TodoList) extends State {
    def getList() = list
  }

  case object Shutdown extends State {
    def getList() = TodoList.default()
  }

  def default(): State = State.Menu(TodoList.default())
}
