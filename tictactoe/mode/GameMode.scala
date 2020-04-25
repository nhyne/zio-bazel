package dev.nhyne.tictactoe.mode

import dev.nhyne.tictactoe.domain.State
import zio.ZIO

trait GameMode {
    val gameMode: GameMode.Service[Any]
}

object GameMode {
    trait Service[R] {
        def process(input: String, state: State.Game): ZIO[R, Nothing, State]
        def render(state: State.Game): ZIO[R, Nothing, String]
    }

    object > extends GameMode.Service[GameMode] {
        def process(input: String, state: State.Game) = {
            ZIO.accessM(_.gameMode.process(input, state))
        }

        def render(state: State.Game) = {
            ZIO.accessM(_.gameMode.render(state))
        }
    }
}
