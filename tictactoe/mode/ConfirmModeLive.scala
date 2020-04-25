package dev.nhyne.tictactoe.mode

import dev.nhyne.tictactoe.parser.ConfirmCommandParser

trait ConfirmModeLive extends ConfirmMode {
    val confirmCommandParser: ConfirmCommandParser.Service[Any]
}
