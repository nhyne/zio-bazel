package dev.nhyne.todo.mode

import dev.nhyne.todo.domain.{State, TodoItem}
import zio.{Has, ZIO, ZLayer}
import zio.console.{Console, getStrLn, putStrLn}
import java.io.IOException

object TodoItemCreator {
  trait Service {
    def createTask(state: State): ZIO[TaskCreator, IOException, State]
  }

  type TaskCreator = Has[Service] with Console

  val live = ZLayer.succeed(new Service {
    def createTask(state: State): ZIO[TaskCreator, IOException, State] =
      for {
        _ <- putStrLn("What do you need to get done?")
        taskTitle <- getStrLn
        _ <- putStrLn("Enter a description.")
        taskDescription <- getStrLn
        task = TodoItem(
          title = taskTitle,
          description = taskDescription,
          listId = 2
        )
        newList = State.Menu(state.getList().addTask(task))

      } yield newList

  })

  def createTask(state: State): ZIO[TaskCreator, IOException, State] =
    ZIO.accessM[TaskCreator](_.get.createTask(state))
}
