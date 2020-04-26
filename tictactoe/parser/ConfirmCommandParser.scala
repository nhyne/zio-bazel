package dev.nhyne.tictactoe.parser

import dev.nhyne.tictactoe.domain.ConfirmCommand
import zio.ZIO

trait ConfirmCommandParser {
    val confirmCommandParser: ConfirmCommandParser.Service[Any]
}

object ConfirmCommandParser {
    trait Service[R] {
        def parse(input: String): ZIO[R, Nothing, ConfirmCommand]
    }
    object > extends ConfirmCommandParser.Service[ConfirmCommandParser] {
        def parse(input: String) = ZIO.accessM(_.confirmCommandParser.parse(input))
    }
}
