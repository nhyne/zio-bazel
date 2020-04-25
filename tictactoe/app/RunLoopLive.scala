package dev.nhyne.tictactoe.app

import dev.nhyne.tictactoe.cli.Terminal
import dev.nhyne.tictactoe.domain.State

trait RunLoopLive extends RunLoop {

    val controller: Controller.Service[Any]
    val terminal: Terminal.Service[Any]

    final val runLoop = new RunLoop.Service[Any] {
        def step(state: State) =
            for {
                x = 4
            } yield {
                x
            }
    }
}
