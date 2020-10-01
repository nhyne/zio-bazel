package dev.nhyne.parallel

import java.io.IOException

import zio.{
  clock,
  console,
  random,
  App,
  ExitCode,
  Schedule,
  UIO,
  URIO,
  ZIO,
  ZLayer
}
import zio.duration._
import zio.console.putStrLn
import zio.random.nextIntBounded

object Main extends App {

  type Messages = zio.Has[Main.ExponentialMessages]
  trait ExponentialMessages {
    def message(
      msg: String
    ): ZIO[random.Random with console.Console with clock.Clock, Throwable, Unit]
  }
  val live =
    ZLayer.succeed(new Main.Live {})

  trait Live extends Main.ExponentialMessages {
    def message(
      msg: String
    ): ZIO[
      random.Random with console.Console with clock.Clock,
      Throwable,
      Unit
    ] =
      for {
        time <- nextIntBounded(1000)
        _ <- putStrLn(s"Sleeping for $time")
        _ <- putStrLn(msg)
        _ = Thread.sleep(time)
        _ <- ZIO.getOrFail(None)
      } yield ()
  }

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
