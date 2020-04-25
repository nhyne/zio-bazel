package dev.nhyne.tictactoe.app

import zio.ZIO
import dev.nhyne.tictactoe.domain.State

trait Controller {
    val controller: Controller.Service[Any]
}

object Controller {
    trait Service[R] {
        def process(input: String, state: State): ZIO[R, Unit, State]

        def render(state: State): ZIO[R, Nothing, String]
    }

    object > extends Controller.Service[Controller] {
        def process(input: String, state: State) = {
            ZIO.accessM(_.controller.process(input, state))
        }

        def render(state: State) = {
            ZIO.accessM(_.controller.render(state))
        }
    }
}
