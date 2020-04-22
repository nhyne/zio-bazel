package dev.nhyne.tictactoe.parser

import dev.nhyne.tictactoe.domain.MenuCommand
import zio.UIO

trait MenuCommandParserLive extends MenuCommandParser {
    val menuCommandParser = new MenuCommandParser.Service[Any] {
        def parse(input: String): UIO[MenuCommand] = ???
    }
}
