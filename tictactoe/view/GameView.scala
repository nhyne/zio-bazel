package dev.nhyne.tictactoe.view

import dev.nhyne.tictactoe.domain.{
  Field,
  GameMessage,
  GameResult,
  Piece,
  Player
}
import zio.ZIO

trait GameView {

  val gameView: GameView.Service[Any]
}

object GameView {

  trait Service[R] {

    def header(result: GameResult,
               turn: Piece,
               player: Player): ZIO[R, Nothing, String]

    def content(board: Map[Field, Piece],
                result: GameResult): ZIO[R, Nothing, String]

    def footer(message: GameMessage): ZIO[R, Nothing, String]
  }

  object > extends GameView.Service[GameView] {
    def header(result: GameResult, turn: Piece, player: Player) =
      ZIO.accessM(_.gameView.header(result, turn, player))

    def content(board: Map[Field, Piece], result: GameResult) =
      ZIO.accessM(_.gameView.content(board, result))

    def footer(message: GameMessage) = ZIO.accessM(_.gameView.footer(message))
  }
}
