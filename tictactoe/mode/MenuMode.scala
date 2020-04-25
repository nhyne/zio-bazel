package dev.nhyne.tictactoe.mode

import dev.nhyne.tictactoe.domain.State
import zio.ZIO

trait MenuMode {
    val menuMode: MenuMode.Service[Any]
}

object MenuMode {
    trait Service[R] {
        def process(input: String, state: State.Menu): ZIO[R, Nothing, State]
        def render(state: State.Menu): ZIO[R, Nothing, String]
    }

    object > extends MenuMode.Service[MenuMode] {
        def process(input: String, state: State.Menu) = {
            ZIO.accessM(_.menuMode.process(input, state))
        }

        def render(state: State.Menu) = {
            ZIO.accessM(_.menuMode.render(state))
        }
    }
}
