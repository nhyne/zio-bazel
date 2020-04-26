package dev.nhyne.tictactoe.view

import dev.nhyne.tictactoe.domain.MenuMessage
import zio.ZIO

trait MenuView {

  val menuView: MenuView.Service[Any]
}

object MenuView {

  trait Service[R] {

    val header: ZIO[R, Nothing, String]

    def content(isSuspended: Boolean): ZIO[R, Nothing, String]

    def footer(message: MenuMessage): ZIO[R, Nothing, String]
  }

  object > extends MenuView.Service[MenuView] {
    def content(isSuspended: Boolean) =
      ZIO.accessM(_.menuView.content(isSuspended))
    def footer(message: MenuMessage) = ZIO.accessM(_.menuView.footer(message))
    val header = ZIO.accessM(_.menuView.header)
  }
}
