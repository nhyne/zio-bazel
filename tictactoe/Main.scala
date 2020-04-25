package dev.nhyne.tictactoe

import zio.console.Console
import zio.{App, UIO, ZIO, console}

object TicTacToe extends App {
  val program: ZIO[Console, Int, Unit] = console.putStrLn("Hello World!")

  def run(args: List[String]): ZIO[Console, Nothing, Int] =
    for {
      env <- UIO.succeed(zio.console.Console.live)
      out <- program
//        .provide(env)
        .foldM(
          error => console.putStrLn("failure") *> UIO.succeed(1),
          _ => UIO.succeed(0)
        )
    } yield {
      out
    }
}
