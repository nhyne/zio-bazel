package dev.nhyne.todo.mode

import dev.nhyne.todo.domain.State
import zio.{Has, UIO, ZIO, ZLayer}

trait MenuMode {
  val menuMode: MenuMode.Service[Any]
}

object MenuMode {
  trait Service {
    // This Any feels weird. Should be more specific about what requirements are needed?
    def process(input: String, state: State): ZIO[Any, Nothing, State]
  }

  type MenuMode = Has[Service]
//  object > extends MenuMode.Service[MenuMode] {
//    def process(input: String, todoList: TodoList) = {
//      ZIO.accessM(_.menuMode.process(input, todoList))
//    }
//  }

  val live = ZLayer.succeed(new Service {
    def process(input: String, state: State): UIO[State] =
      menuCommandParser.parse(input) map {
        case MenuCommand.NewTask =>
          for {
            newTask <- createTask
          } yield UIO.succeed(list.addTask(newTask))
        case MenuCommand.Invalid =>
          UIO.succeed(list)
      }
  })

  def process(input: String, state: State): ZIO[MenuMode, Nothing, State] =
    ZIO.accessM[MenuMode](_.get.process(input, state))
}

trait MenuModeLive extends MenuMode {

  val menuCommandParser: MenuCommandParser.Service

  val createTask: ZIO[Console, IOException, Task] = for {
    _ <- putStrLn("What do you need to get done?")
    taskTitle <- getStrLn
    _ <- putStrLn("Enter a description.")
    taskDescription <- getStrLn
    task = Task(taskTitle, taskDescription)
  } yield task

}
