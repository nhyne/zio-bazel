package dev.nhyne.tictactoe.logic

import dev.nhyne.tictactoe.domain.{Field, GameResult, Piece}
import zio.ZIO
import zio.test.BoolAlgebra

trait GameLogic {
    val gameLogic: GameLogic.Service[Any]
}

object GameLogic {
    trait Service[R] {
        def putPiece(board: Map[Field, Piece], field: Field, piece: Piece): ZIO[R, Unit, Map[Field, Piece]]
        def gameResult(board: Map[Field, Piece]): ZIO[R, Unit, GameResult]
        def nextTurn(currentTurn: Piece): ZIO[R, Nothing, Piece]
    }
}
