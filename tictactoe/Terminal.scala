package dev.nhyne.tictactoe.cli

import zio.ZIO

trait Terminal {
  val terminal: Terminal.Service[Any]
}
object Terminal {
  trait Service[R] {
    val getUserInput: ZIO[R, Nothing, String]
    def display(frame: String): ZIO[R, Nothing, Unit]
  }
}
