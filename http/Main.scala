package dev.nhyne.http

import dev.nhyne.http.HttpTestService.{HttpService, HttpTestService}
import zio._
import zio.console.{putStrLn, Console}

object Main extends App {

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    program
      .provideSomeLayer[ZEnv](HttpTestService.live)
      .fold(
        _ => ExitCode(1),
        _ => ExitCode(0)
      )

  val program: ZIO[HttpService with Console, String, String] = for {
    _ <- putStrLn("something")
    a <- HttpTestService.something(1)
    _ <- putStrLn(a)
  } yield a
}
