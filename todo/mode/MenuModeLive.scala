package dev.nhyne.domain.mode

import java.io.IOException

import dev.nhyne.todo.Todo.taskList
import dev.nhyne.todo.mode.MenuMode
import dev.nhyne.todo.parser.MenuCommandParser
import dev.nhyne.todo.domain.{MenuCommand, Task, TodoList}
import zio.{UIO, ZIO}
import zio.console.{Console, getStrLn, putStrLn}

trait MenuModeLive extends MenuMode {

  val menuCommandParser: MenuCommandParser.Service[Any]

  val createTask: ZIO[Console, IOException, Task] = for {
    _ <- putStrLn("What do you need to get done?")
    taskTitle <- getStrLn
    _ <- putStrLn("Enter a description.")
    taskDescription <- getStrLn
    task = Task(taskTitle, taskDescription)
  } yield task

  val menuMode = new MenuMode.Service[Any] {

    def process(input: String, list: TodoList): UIO[TodoList] =
      menuCommandParser.parse(input) map {
        case MenuCommand.NewTask =>
          for {
            newTask <- createTask
          } yield UIO.succeed(list.addTask(newTask))
        case MenuCommand.Invalid =>
          UIO.succeed(list)
      }
  }
}
