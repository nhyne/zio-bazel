package dev.nhyne.tictactoe.cli

import zio.ZIO
import zio.console.Console

trait TerminalLive extends Terminal {
  val console: Console.Service

  final val terminal = new Terminal.Service[Any] {
    val getUserInput = console.getStrLn.orDie

    def display(frame: String): ZIO[Any, Nothing, Unit] =
      for {
        _ <- console.putStr(TerminalLive.ANSI_CLEARSCREEN)
        _ <- console.putStrLn(frame)
      } yield {}
  }
}

object TerminalLive {
  val ANSI_CLEARSCREEN: String = "\u001b[H\u001b[2J"
}
