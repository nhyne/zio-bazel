package dev.nhyne.tictactoe.logic

import dev.nhyne.tictactoe.domain.{Board, Field, GameResult, Piece}
import zio.{IO, UIO, ZIO}

trait GameLogicLive extends GameLogic {
    val gameLogic = new GameLogic.Service[Any] {
        def putPiece(board: Map[Field, Piece], field: Field, piece: Piece): IO[Unit, Map[Field, Piece]] =
            board.get(field) match {
                case None => IO.succeed(board.updated(field, piece))
                case _ => IO.fail(())
            }

        def gameResult(board: Map[Field, Piece]): UIO[GameResult] = {
            val pieces: Map[Piece, Set[Field]] =
                board.groupBy(_._2)
                .mapValues(_.keys.toSet)
                .withDefaultValue(Set.empty[Field])

            val xWin: Boolean = Board.wins.exists(_ subsetOf pieces(Piece.X))
            val oWin: Boolean = Board.wins.exists(_ subsetOf pieces(Piece.O))
            val boardFull: Boolean = board.size == 9

            if (xWin && oWin) ZIO.die(new IllegalStateException("It should not be possible for both players to win"))
            else if (xWin) UIO.succeed(GameResult.Win(Piece.X))
            else if (oWin) UIO.succeed(GameResult.Win(Piece.O))
            else if (boardFull) UIO.succeed(GameResult.Draw)
            else UIO.succeed(GameResult.Ongoing)
        }

        def nextTurn(current: Piece): UIO[Piece] = UIO.succeed(current) map {
            case Piece.X => Piece.O
            case Piece.O => Piece.X
        }
    }
}
