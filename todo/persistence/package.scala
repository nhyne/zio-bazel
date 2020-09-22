package dev.nhyne.todo

import cats.effect.{Blocker, Resource}
import dev.nhyne.todo.configuration.DbConfig
import doobie.hikari.HikariTransactor
import zio.interop.catz._
import zio.Task

import scala.concurrent.ExecutionContext

package object persistence {
  def mkBaseTransactor(
    conf: DbConfig,
    connectEC: ExecutionContext
  ): Resource[Task, HikariTransactor[Task]] =
    for {
      blocker <- Blocker[Task]
      tnxr <- HikariTransactor.newHikariTransactor[Task](
        driverClassName = "org.postgresql.Driver",
        url = conf.url,
        user = conf.user,
        pass = conf.password,
        connectEC = connectEC,
        blocker = blocker
      )
    } yield tnxr
}
