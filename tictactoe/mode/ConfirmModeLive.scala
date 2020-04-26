package dev.nhyne.tictactoe.mode

import dev.nhyne.tictactoe.domain.{ConfirmCommand, State, ConfirmMessage}
import zio.UIO
import dev.nhyne.tictactoe.parser.ConfirmCommandParser
import dev.nhyne.tictactoe.view.ConfirmView

trait ConfirmModeLive extends ConfirmMode {

  val confirmCommandParser: ConfirmCommandParser.Service[Any]
  val confirmView: ConfirmView.Service[Any]

  val confirmMode = new ConfirmMode.Service[Any] {

    def process(input: String, state: State.Confirm): UIO[State] =
      confirmCommandParser.parse(input) map {
        case ConfirmCommand.Yes     => state.confirmed
        case ConfirmCommand.No      => state.declined
        case ConfirmCommand.Invalid => state.copy(message = ConfirmMessage.InvalidCommand)
      }

    def render(state: State.Confirm): UIO[String] =
      for {
        header  <- confirmView.header(state.action)
        content <- confirmView.content
        footer  <- confirmView.footer(state.message)
      } yield List(header, content, footer).mkString("\n\n")
  }
}
