package dev.nhyne.todo.configuration

import zio.{Task, ZIO, ZLayer}
import pureconfig._
import pureconfig.generic.auto._

case class Config(api: ApiConfig, dbConfig: DbConfig)
case class ApiConfig(endpoint: String, port: Int)
case class DbConfig(url: String, user: String, password: String)

object Configuration {
  type Configuration = zio.Has[Configuration.Service]
  trait Service {
    val load: Task[Config]
  }

  val live: ZLayer[Any, Nothing, Configuration] = ZLayer.succeed(new Live {})

  val load: ZIO[Configuration, Throwable, Config] = ZIO.accessM(_.get.load)

}

trait Live extends Configuration.Service {
  val load: Task[Config] = Task.effect(ConfigSource.default.loadOrThrow[Config])
}
