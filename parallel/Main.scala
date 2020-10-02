package dev.nhyne.parallel

import dev.nhyne.parallel.TimedMessageService.TimedMessage
import zio.{App, ExitCode, UIO, URIO, ZIO}
import zio.console.Console

object Main extends App {
  type ProgramEnv = Console

  def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {

//    val tenTimes = Schedule.recurs(10)
//    val schedule = Schedule.exponential(1.seconds) && tenTimes
    val timedMessage = TimedMessageService.live
//    val randomSleep = for {
//      time <- nextIntBounded(1000)
//      _ <- putStrLn(s"Sleeping for $time")
//
//      _ = Thread.sleep(time)
//      _ <- ZIO.getOrFail(None)
//    } yield ()

    val program: ZIO[TimedMessage, Nothing, Unit] = for {
      _ <-
        TimedMessageService.message("cool", zio.duration.Duration.Finite(1000))
    } yield ()

    program
      .provideSomeLayer[zio.ZEnv](timedMessage)
      .foldM(
        _ => UIO.succeed(ExitCode.success),
        _ => UIO.succeed(ExitCode.failure)
      )
  }
}
