package dev.nhyne.tictactoe.mode

import dev.nhyne.tictactoe.domain.State
import zio.ZIO

trait ConfirmMode {
    val confirmMode: ConfirmMode.Service[Any]
}

object ConfirmMode {
    trait Service[R] {
        def process(input: String, state: State.Confirm): ZIO[R, Nothing, State]
        def render(state: State.Confirm): ZIO[R, Nothing, String]
    }

    object > extends ConfirmMode.Service[ConfirmMode] {
        def process(input: String, state: State.Confirm) = {
            ZIO.accessM(_.confirmMode.process(input, state))
        }

        def render(state: State.Confirm) = {
            ZIO.accessM(_.confirmMode.render(state))
        }
    }
}
