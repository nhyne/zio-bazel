package dev.nhyne.tictactoe.app

import zio.ZIO
import dev.nhyne.tictactoe.domain.State

trait RunLoop {
    val runLoop: RunLoop.Service[Any]
}

object RunLoop {
    trait Service[R] {
        def step(state: State): ZIO[R, Unit, State]
    }

}
