package dev.nhyne.todo

import dev.nhyne.todo.domain.{MenuCommand, State}
import dev.nhyne.todo.mode.TaskCreator
import dev.nhyne.todo.parser.MenuCommandParser
import zio.console.{Console, getStrLn, putStrLn}
import zio.{App, UIO, ZEnv, ZIO}
import java.io.IOException

object Todo extends App {

  val env = MenuCommandParser.live ++ TaskCreator.live ++ Console.live ++ PostgresConnection.live
  type programEnv = MenuCommandParser.MenuCommandParser
    with PostgresConnection.PostgresConnection
    with TaskCreator.TaskCreator
    with Console

  val program = for {
    _ <- putStrLn("Beginning todo list")
    state = State.default()
    _ <- programLoop(state)
  } yield ()

  def programLoop(
      state: State
  ): ZIO[programEnv, IOException, State] =
    for {
      _ <- putStrLn("What would you like to do? (new, exit, display)")
      _ <- PostgresConnection.printVal()
      inputCommand <- getStrLn
      command <- MenuCommandParser.parse(inputCommand)
      newState <- command match {
        case MenuCommand.NewTask => TaskCreator.createTask(state = state)
        case MenuCommand.Display => UIO.succeed(state).tap(x => putStrLn(s"$x"))
        case MenuCommand.Exit    => UIO.succeed(state)
        case _                   => UIO.succeed(state).tap(_ => putStrLn("Invalid Command"))
      }
      _ <- if (command == MenuCommand.Exit) UIO.succeed(newState)
      else programLoop(newState)
    } yield newState

  def run(args: List[String]): ZIO[ZEnv, Nothing, Int] =
    (for {
      out <- program
        .foldM(_ => UIO.succeed(1), _ => UIO.succeed(0))
    } yield out)
      .provideCustomLayer(env)

}
