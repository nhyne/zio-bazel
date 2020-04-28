package dev.nhyne.todo.mode

import dev.nhyne.todo.domain.{State, Task}
import zio.{Has, ZIO, ZLayer}
import zio.console.{Console, getStrLn, putStrLn}
import java.io.IOException

object TaskCreatorMode {
    trait Service {
        def createTask(state: State): ZIO[TaskCreatorMode, IOException, State]
    }

    type TaskCreatorMode = Has[Service] with Console

    val live = ZLayer.succeed(new Service {
        def createTask(state: State): ZIO[TaskCreatorMode, IOException, State] = for {
             _ <- putStrLn("What do you need to get done?")
        taskTitle <- getStrLn
        _ <- putStrLn("Enter a description.")
        taskDescription <- getStrLn
        task = Task(taskTitle, taskDescription)
            newList = State.Menu(state.getList().addTask(task))

      } yield newList

    })

    def createTask(state: State): ZIO[TaskCreatorMode, IOException, State] =
        ZIO.accessM[TaskCreatorMode](_.get.createTask(state))
}
