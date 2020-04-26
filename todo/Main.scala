package dev.nhyne.todo

import zio.console.Console
import zio.{App, UIO, ZIO, console}

object Todo extends App {
  val program: ZIO[Console, Int, Unit] = console.putStrLn("Hello World!")

  def run(args: List[String]): ZIO[Console, Nothing, Int] =
    for {
      out <- program
        .foldM(
          _ => console.putStrLn("failure") *> UIO.succeed(1),
          _ => UIO.succeed(0)
        )
    } yield {
      out
    }
}
