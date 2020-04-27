package dev.nhyne.todo.mode

import java.io.IOException

import dev.nhyne.todo.Todo
import dev.nhyne.todo.domain.{MenuCommand, State, Task, TodoList}
import dev.nhyne.todo.parser.MenuCommandParser
import zio.console.{Console, getStrLn, putStrLn}
import zio.{Has, UIO, ZIO, ZLayer}

trait MenuMode {
  val menuMode: MenuMode.Service
}

object MenuMode {
  trait Service {
    // This Any feels weird. Should be more specific about what requirements are needed?
    def process(
      input: String,
      state: State
    ): ZIO[MenuMode with MenuCommandParser, Nothing, State]
  }

  type MenuCommandParser = Has[MenuCommandParser.Service]
  type MenuMode = Has[Service]

  val live = ZLayer.succeed(new Service {
    def process(
      input: String,
      state: State
    ): ZIO[MenuMode with MenuCommandParser, Nothing, State] =
      ZIO.accessM[MenuCommandParser](_.get.parse(input) map {
        case MenuCommand.NewTask =>
          State.NewTask(state.getList(), None)
        case MenuCommand.Invalid =>
          State.Menu(state.getList())
      })
  })

  def process(
    input: String,
    state: State
  ): ZIO[MenuMode with MenuCommandParser, Nothing, State] =
    ZIO.accessM[MenuMode with MenuCommandParser](_.get.process(input, state))

  val createTask: ZIO[Console, IOException, Task] = for {
    _ <- putStrLn("What do you need to get done?")
    taskTitle <- getStrLn
    _ <- putStrLn("Enter a description.")
    taskDescription <- getStrLn
    task = Task(taskTitle, taskDescription)
  } yield task
}
