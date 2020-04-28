package dev.nhyne.todo

import dev.nhyne.todo.domain.{MenuCommand, State, Task, TodoList}
import dev.nhyne.todo.mode.TaskCreatorMode
import dev.nhyne.todo.parser.MenuCommandParser
import zio.console.{Console, getStrLn, putStrLn}
import zio.{App, Has, UIO, ZEnv, ZIO, ZLayer, console}
import java.io.IOException

object Todo extends App {

  val env = MenuCommandParser.live ++ TaskCreatorMode.live ++ Console.live

  val program = for {
    _ <- putStrLn("Beginning todo list")
    state = State.default()
    _ <- programLoop(state)
  } yield ()

  val createTask: ZIO[Console, IOException, Task] = for {
    _ <- putStrLn("What do you need to get done?")
    taskTitle <- getStrLn
    _ <- putStrLn("Enter a description.")
    taskDescription <- getStrLn
    task = Task(taskTitle, taskDescription)
  } yield task

  def programLoop(
    state: State
  ): ZIO[MenuCommandParser.MenuCommandParser with TaskCreatorMode.TaskCreatorMode with Console, IOException, State] =
    for {
      _ <- putStrLn("What would you like to do? (new)")
      inputCommand <- getStrLn
      command <- MenuCommandParser.parse(inputCommand)
        newState <- command match {
            case MenuCommand.NewTask => TaskCreatorMode.createTask(state = state)
            case _ => UIO.succeed(state)
        }
        _ <- putStrLn(s"Current state is: $newState")
        _ <- programLoop(newState)
    } yield newState

  def run(args: List[String]): ZIO[ZEnv, Nothing, Int] =
    (for {
      out <- program
        .foldM(_ => UIO.succeed(1), _ => UIO.succeed(0))
    } yield out)
      .provideCustomLayer(env)

}
