package dev.nhyne.parallel

import zio.{App, ExitCode, URIO}

object Main extends App {
  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {}
}
