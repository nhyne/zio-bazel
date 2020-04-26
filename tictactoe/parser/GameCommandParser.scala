package dev.nhyne.tictactoe.parser

import dev.nhyne.tictactoe.domain.GameCommand
import zio.ZIO

trait GameCommandParser {
    val gameCommandParser: GameCommandParser.Service[Any]
}

object GameCommandParser {
    trait Service[R] {
        def parse(input: String): ZIO[R, Nothing, GameCommand]
    }

    object > extends GameCommandParser.Service[GameCommandParser] {
        def parse(input: String) = ZIO.accessM(_.gameCommandParser.parse(input))
    }
}
