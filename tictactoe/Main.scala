package dev.nhyne.tictactoe

import zio.console.Console
import zio.random.Random
import zio.{App, UIO, ZEnv, ZIO, console}
import dev.nhyne.tictactoe.domain.State
import dev.nhyne.tictactoe.app._

object TicTacToe extends App {
  val program = {
    def loop(state: State): ZIO[app.RunLoop, Nothing, Unit] =
      app.RunLoop.>.step(state)
        .foldM(_ => UIO.unit, nextState => loop(nextState))

    loop(State.default)
  }

  def run(args: List[String]): ZIO[ZEnv, Nothing, Int] =
    for {
      env <- prepareEnvironment
      out <- program
        .provide(env)
              .as(0)

    } yield out

  private val prepareEnvironment =
    UIO.succeed(
      new app.ControllerLive
        with app.RunLoopLive
        with cli.TerminalLive
        with logic.GameLogicLive
        with logic.OpponentAiLive
        with mode.ConfirmModeLive
        with mode.GameModeLive
        with mode.MenuModeLive
        with parser.ConfirmCommandParserLive
        with parser.GameCommandParserLive
        with parser.MenuCommandParserLive
        with view.ConfirmViewLive
        with view.GameViewLive
        with view.MenuViewLive
        with Console.Service
        with Random.Service {}
    )
}
