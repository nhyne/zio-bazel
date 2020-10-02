package dev.nhyne.parallel

import zio.{Has, ZIO, ZLayer}
import zio.duration.Duration

object TimedMessageService {
  type TimedMessage = Has[Service]
  trait Service {
    def message(msg: String, delay: Duration): ZIO[TimedMessage, Nothing, Unit]
  }

  val live: ZLayer[
    zio.ZEnv,
    Throwable,
    TimedMessage
  ] = ZLayer.succeed(new Live {})

  def message(msg: String, delay: Duration): ZIO[TimedMessage, Nothing, Unit] =
    ZIO.accessM(_.get.message(msg, delay))
}

trait Live extends TimedMessageService.Service {
  def message(msg: String, delay: Duration) = {
    Thread.sleep(delay.toMillis)
    ZIO.succeed(println(msg))
  }
}
