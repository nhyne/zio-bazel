package dev.nhyne.todo

import dev.nhyne.todo.domain.{TodoItem, TodoList}
import dev.nhyne.todo.persistence.{
  TodoItemPersistenceService,
  TodoListPersistenceService
}
import dev.nhyne.todo.persistence.TodoItemPersistenceService.TaskPersistence
import zio.RIO
import caliban.GraphQL.graphQL
import caliban.wrappers.Wrappers.{printSlowQueries, timeout}
import zio.duration._
import caliban.{GraphQL, RootResolver}
import caliban.schema.GenericSchema
import caliban.GraphQL.graphQL
import dev.nhyne.todo.persistence.TodoListPersistenceService.TodoPersistence
import zio.clock.Clock
import zio.console.Console

object GraphqlService
    extends GenericSchema[TaskPersistence with TodoPersistence] {
  case class GetTodoItemArgs(todoId: Int)
  case class GetTodoListArgs(todoListId: Int)

  case class Queries(
      getTodo: GetTodoItemArgs => RIO[TaskPersistence, TodoItem],
      getTodoList: GetTodoListArgs => RIO[TodoPersistence, TodoList]
  )

  val queries = Queries(
    getTodo = args => TodoItemPersistenceService.getTodoItem(args.todoId),
    getTodoList =
      args => TodoListPersistenceService.getTodoList(args.todoListId)
  )

  implicit val queriesSchema = gen[Queries]

  val api
      : GraphQL[Console with Clock with TaskPersistence with TodoPersistence] =
    graphQL(
      RootResolver(
        queries
      )
    ).withWrapper(timeout(3.seconds))
      .withWrapper(printSlowQueries(500.milliseconds))
}
