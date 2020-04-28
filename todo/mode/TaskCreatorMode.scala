package dev.nhyne.todo.mode

import dev.nhyne.todo.domain.Task
import zio.{Has, ZIO, ZLayer, UIO}
import zio.console.{Console, putStrLn, getStrLn}
import java.io.IOException

object TaskCreatorMode {
    trait Service {
        def createTask(): ZIO[TaskCreatorMode, IOException, Task]
    }

    type TaskCreatorMode = Has[Service] with Console

    val live = ZLayer.succeed(new Service {
        def createTask(): ZIO[TaskCreatorMode, IOException, Task] = for {
             _ <- putStrLn("What do you need to get done?")
        taskTitle <- getStrLn
        _ <- putStrLn("Enter a description.")
        taskDescription <- getStrLn
        task = Task(taskTitle, taskDescription)
      } yield task

    })

    def createTask(): ZIO[TaskCreatorMode, IOException, Task] =
        ZIO.accessM[TaskCreatorMode](_.get.createTask())
}
