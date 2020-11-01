package dev.nhyne.http

import zio._
import zio.console.{putStrLn, Console}

object Main extends App {

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    program.as(ExitCode(1))

  val program: ZIO[Console, Nothing, Unit] = for {
    _ <- putStrLn("something")
  } yield ()

//    .tapError(err => putStrLn(s"Execution failed with: $err"))
}
