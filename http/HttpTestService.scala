package dev.nhyne.http

import zio.{Has, IO, ZIO, ZLayer}

object HttpTestService {

  type HttpService = Has[HttpTestService]

  trait HttpTestService {
    def something(s: Int): ZIO[HttpService, String, String]
  }

  val live: ZLayer[Any, Nothing, HttpService] =
    ZLayer.fromFunction(_ => HttpTestServiceImpl)

  def something(s: Int): ZIO[HttpService, String, String] =
    ZIO.accessM[HttpService](_.get.something(s))

  object HttpTestServiceImpl extends HttpTestService {
    def something(s: Int) =
      for {
        a <-
          if (s == 0) ZIO.fail("we got a zero!")
          else ZIO.succeed("hello katherine!")
      } yield a
  }
}
