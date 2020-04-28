package dev.nhyne.todo.domain

sealed trait MenuCommand

object MenuCommand {
    case object NewTask extends MenuCommand
    case object Exit extends MenuCommand
    case object Display extends MenuCommand
    case object Invalid extends MenuCommand
}
