package dev.nhyne.tictactoe.domain

sealed trait GameCommand

object GameCommand {
    case object Menu extends GameCommand
    case object Invalid extends GameCommand

    case class Put(field: Field) extends GameCommand
}
