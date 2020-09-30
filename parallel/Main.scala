package dev.nhyne.parallel

import java.io.IOException

import zio.{App, ExitCode, Schedule, UIO, URIO, ZIO}
import zio.duration._
import zio.console.{putStrLn}
import zio.random.nextIntBounded

object Main extends App {
  def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {

    val tenTimes = Schedule.recurs(10)
    val schedule = Schedule.exponential(1.seconds) && tenTimes

    val randomSleep = for {
      time <- nextIntBounded(1000)
      _ <- putStrLn(s"Sleeping for $time")

      _ = Thread.sleep(time)
      _ <- ZIO.getOrFail(None)
    } yield ()

    val program: ZIO[zio.ZEnv, IOException, ExitCode] =
      randomSleep
        .retry(schedule)
        .foldM(
          _ => UIO.succeed(ExitCode.success),
          _ => UIO.succeed(ExitCode.failure)
        )

    program
      .foldM(
        _ => UIO.succeed(ExitCode.success),
        _ => UIO.succeed(ExitCode.failure)
      )
  }
}

trait ExponentialMessages {}
