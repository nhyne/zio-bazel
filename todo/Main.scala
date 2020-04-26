package dev.nhyne.todo

import java.io.IOException
import dev.nhyne.todo.domain.{Task, TodoList, MenuCommand}
import dev.nhyne.todo.parser.{MenuCommandParser, MenuCommandParserLive}
import zio.console.{Console, getStrLn, putStrLn}
import zio.{App, UIO, ZIO, console}

object Todo extends App {

  val program = for {
    _ <- putStrLn("Beginning todo list")
    list = TodoList(Seq.empty)
    _ <- programLoop(list)
  } yield ()

  def programLoop(
    list: TodoList
  ): ZIO[MenuCommandParserLive with Console, IOException, TodoList] =
    for {
      _ <- putStrLn("What would you like to do? (new)")
      inputCommand <- getStrLn
      command <- MenuCommandParser.>.parse(inputCommand)
      x <- command match {
        case MenuCommand.NewTask => putStrLn("cool")
        case _                   => putStrLn("not cool")
      }
    } yield TodoList(Seq.empty)

  def run(args: List[String]): ZIO[Console, Nothing, Int] =
    for {
      env <- prepEnv
      out <- program
        .provide(env)
        .foldM(_ => UIO.succeed(1), _ => UIO.succeed(0))
    } yield out

  def prepEnv =
    UIO.succeed(new MenuCommandParserLive with zio.console.Console.Service)
}
