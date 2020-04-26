package dev.nhyne.tictactoe.view

import dev.nhyne.tictactoe.domain.{ConfirmAction, ConfirmMessage}
import zio.ZIO

trait ConfirmView {

  val confirmView: ConfirmView.Service[Any]
}

object ConfirmView {

  trait Service[R] {

    def header(action: ConfirmAction): ZIO[R, Nothing, String]

    val content: ZIO[R, Nothing, String]

    def footer(message: ConfirmMessage): ZIO[R, Nothing, String]
  }

  object > extends ConfirmView.Service[ConfirmView] {
    def header(action: ConfirmAction) =
      ZIO.accessM(_.confirmView.header(action))

    def footer(message: ConfirmMessage) =
      ZIO.accessM(_.confirmView.footer(message))

    val content =
      ZIO.accessM(_.confirmView.content)
  }
}
