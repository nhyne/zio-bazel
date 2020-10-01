package dev.nhyne.parallel

import java.io.IOException

import dev.nhyne.parallel.TimedMessageService
import dev.nhyne.parallel.TimedMessageService.TimedMessage
import zio.{App, ExitCode, Schedule, UIO, URIO, ZIO, ZLayer, clock, console, random}
import zio.duration._
import zio.console.{Console, putStrLn}
import zio.random.nextIntBounded

object Main extends App {
  type ProgramEnv = Console with TimedMessage

  def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {

    val tenTimes = Schedule.recurs(10)
    val schedule = Schedule.exponential(1.seconds) && tenTimes

//    val randomSleep = for {
//      time <- nextIntBounded(1000)
//      _ <- putStrLn(s"Sleeping for $time")
//
//      _ = Thread.sleep(time)
//      _ <- ZIO.getOrFail(None)
//    } yield ()

    val program: ZIO[zio.ZEnv, IOException, ExitCode] = for {

    }

    program
      .foldM(
        _ => UIO.succeed(ExitCode.success),
        _ => UIO.succeed(ExitCode.failure)
      )
  }
}
