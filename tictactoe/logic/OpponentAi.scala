package dev.nhyne.tictactoe.logic

import dev.nhyne.tictactoe.domain.{Field, Piece}
import zio.ZIO

trait OpponentAi {
  val opponentAi: OpponentAi.Service[Any]
}

object OpponentAi {

  trait Service[R] {
    def randomMove(board: Map[Field, Piece]): ZIO[R, Unit, Field]
  }

  object > extends OpponentAi.Service[OpponentAi] {
    def randomMove(board: Map[Field, Piece]) = {
      ZIO.accessM(_.opponentAi.randomMove(board))
    }
  }
}
