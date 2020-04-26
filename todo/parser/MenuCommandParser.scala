package dev.nhyne.todo.parser

import dev.nhyne.todo.domain.MenuCommand
import zio.{ZIO, ZLayer, UIO, Has}

object MenuCommandParser {
  trait Service {
    def parse(input: String): ZIO[Any, Nothing, MenuCommand]
  }

  type MenuCommandParser = Has[Service]

  val live = ZLayer.succeed(new Service  {
    def parse(input: String): UIO[MenuCommand] =
      UIO.succeed(input).map {
        case "new task" => MenuCommand.NewTask
        case _          => MenuCommand.Invalid
      }
  })

  def parse(input: String) =
    ZIO.accessM[MenuCommandParser](_.get.parse(input))
}
