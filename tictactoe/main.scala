package dev.nhyne.tictactoe

import zio.{App, ZEnv, ZIO, console}
import zio.console.Console

object TicTacToe extends App {
    val program : ZIO[Console, Int, Unit] = console.putStrLn("Hello World!")

    def run(args: List[String]): ZIO[ZEnv, Nothing, Int] =
        program.foldM(
            error => console.putStrLn(s"Execution failed with: $error") *> ZIO.succeed(1),
            _ => ZIO.succeed(0)
        )
}
