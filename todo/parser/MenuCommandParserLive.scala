package dev.nhyne.todo.parser

import dev.nhyne.todo.domain.MenuCommand
import zio.UIO

trait MenuCommandParserLive extends MenuCommandParser {
  val menuCommandParser = new MenuCommandParser.Service[Any] {
    def parse(input: String): UIO[MenuCommand] =
      UIO.succeed(input).map {
        case "new" => MenuCommand.NewTask
        case _          => MenuCommand.Invalid
      }
  }
}
