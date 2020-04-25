package dev.nhyne.tictactoe.domain

sealed trait MenuMessage

object MenuMessage{
    val empty = new MenuMessage {}
}
