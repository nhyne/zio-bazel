package dev.nhyne.todo

import java.io.IOException
import dev.nhyne.todo.domain.{Task, TodoList, MenuCommand}
import dev.nhyne.todo.parser.MenuCommandParser
import zio.console.{Console, getStrLn, putStrLn}
import zio.{App, UIO, ZIO, console, ZLayer, ZEnv, Has}

object Todo extends App {

  val program = for {
    _ <- putStrLn("Beginning todo list")
    list = TodoList(Seq.empty)
    _ <- programLoop(list)
  } yield ()

  def programLoop(
    list: TodoList
  ): ZIO[MenuCommandParser.MenuCommandParser with Console, IOException, TodoList] =
    for {
      _ <- putStrLn("What would you like to do? (new)")
      inputCommand <- getStrLn
      command <- MenuCommandParser.parse(inputCommand)
      x <- command match {
        case MenuCommand.NewTask => putStrLn("cool")
        case _                   => putStrLn("not cool")
      }
    } yield TodoList(Seq.empty)

  val env = MenuCommandParser.live ++ Console.live

  def run(args: List[String]): ZIO[ZEnv, Nothing, Int] =
    (for {
      out <- program
        .foldM(_ => UIO.succeed(1), _ => UIO.succeed(0))
    } yield out)
      .provideCustomLayer(env)

}
