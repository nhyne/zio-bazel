package dev.nhyne.tictactoe.domain

sealed trait Piece

object Piece {
    case object O extends Piece
    case object X extends Piece
}
