package dev.nhyne.todo

import dev.nhyne.todo.domain.{Task, TodoList}
import zio.console.{Console, getStrLn, putStrLn}
import zio.{App, UIO, ZIO, console}
import java.io.IOException

object Todo extends App {

    val taskList = TodoList(Seq.empty)
  val program: ZIO[Console, Int, Unit] = console.putStrLn("Hello World!")

    val listGen: ZIO[Console, IOException, TodoList] = for {
        _ <- putStrLn("What do you need to get done?")
        taskTitle <- getStrLn
        _ <- putStrLn("Enter a description.")
        taskDescription <- getStrLn
        task = Task(taskTitle, taskDescription)
    } yield taskList.addTask(task)

  def run(args: List[String]): ZIO[Console, Nothing, Int] =
    for {
      list <- listGen.foldM(
          _ => UIO.succeed(1),
          _ => UIO.succeed(0)
      )
    } yield {
      list
    }

}
