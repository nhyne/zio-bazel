package dev.nhyne.parallel

import zio.console.{putStrLn, Console}
import zio.{Has, ZIO, ZLayer}
import zio.duration.Duration

class TimedMessageService {}

object TimedMessageService {
  type TimedMessage = Has[Service]
  trait Service {
    def message(msg: String, delay: Duration): ZIO[Console, Nothing, Unit]
  }

  val live: ZLayer[
    Console,
    Throwable,
    TimedMessage
  ] = ZLayer.succeed(new Live {})

  def message(msg: String, delay: Duration): ZIO[Console, Nothing, Unit] =
    ZIO.accessM[TimedMessage](_.get.message(msg, delay))
}

trait Live extends TimedMessageService.Service {
  def message(msg: String, delay: Duration) = {
    Thread.sleep(delay.toMillis)
    for {
      _ <- putStrLn(msg)
    } yield ()
  }
}
