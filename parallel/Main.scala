package dev.nhyne.parallel

import zio.{App, ExitCode, Schedule, UIO, URIO, ZIO}
import zio.console.putStrLn
import zio.random.nextInt

object Main extends App {
  def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {

    val randomSleep = for {
      time <- nextInt
      _ <- putStrLn(s"Sleeping for $time")
      _ = Thread.sleep(time)
    } yield ()

    val tenTimes = Schedule.recurs(10)

    val program: ZIO[zio.ZEnv, Nothing, ExitCode] =
      randomSleep
        .retry(tenTimes)
        .foldM(
          _ => UIO.succeed(ExitCode.success),
          _ => UIO.succeed(ExitCode.failure)
        )

    program
  }
}
