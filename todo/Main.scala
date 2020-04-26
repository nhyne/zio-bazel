package dev.nhyne.todo

import java.io.IOException
import dev.nhyne.todo.domain.{Task, TodoList, MenuCommand}
import dev.nhyne.todo.parser.MenuCommandParser
import zio.console.{Console, getStrLn, putStrLn}
import zio.{App, UIO, ZIO, console}

object Todo extends App {

    val taskList = TodoList(Seq.empty)
  val program: ZIO[Console, Int, Unit] = console.putStrLn("Hello World!")

    val createTask: ZIO[Console, IOException, TodoList] = for {
        _ <- putStrLn("What do you need to get done?")
        taskTitle <- getStrLn
        _ <- putStrLn("Enter a description.")
        taskDescription <- getStrLn
        task = Task(taskTitle, taskDescription)
    } yield taskList.addTask(task)


    val menuSelection = for {
        x <- getStrLn
        y <- MenuCommandParser.>.parse(x)
    } yield y

  def run(args: List[String]): ZIO[Console, Nothing, Int] =
    for {
        y <- menuSelection
        x <- y match {
            case _ => createTask.foldM(
                _ => UIO.succeed(1),
                x => UIO.succeed(x)
            )
//            case _ => ZIO.succeed(TodoList(Seq.empty))
        }
        _ <- putStrLn("something")
    } yield {
        x
        1
    }

}
