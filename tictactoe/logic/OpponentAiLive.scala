package dev.nhyne.tictactoe.logic

import dev.nhyne.tictactoe.domain.{Piece, Field}
import zio.random.Random
import zio.{ZIO, IO}

trait OpponentAiLive extends OpponentAi {
  val random = Random.Service.live

  val opponentAi = new OpponentAi.Service[Any] {
    def randomMove(board: Map[Field, Piece]) = {
      val unoccupied = (Field.values.toSet -- board.keys.toSet)
      unoccupied.size match {
        case 0 => IO.fail(())
        case n => random.nextInt(n) map (idx => unoccupied.toList(idx))
      }
    }
  }
}
