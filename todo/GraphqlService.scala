package dev.nhyne.todo

import dev.nhyne.todo.domain.{TodoItem, TodoList}
import dev.nhyne.todo.persistence.TodoItemPersistenceService
import dev.nhyne.todo.persistence.TodoItemPersistenceService.TaskPersistence
import zio.RIO
import caliban.GraphQL.graphQL
import caliban.wrappers.Wrappers.{printSlowQueries, timeout}
import zio.duration._
import caliban.{GraphQL, RootResolver}
import caliban.schema.GenericSchema
import caliban.GraphQL.graphQL
import zio.clock.Clock
import zio.console.Console

object GraphqlService extends GenericSchema[TaskPersistence] {
  case class GetTodoItemArgs(id: Int)
    case class GetTodoListArgs(id: Int)

  case class Queries(
      getTodo: GetTodoItemArgs => RIO[TaskPersistence, TodoItem]
  )

  val queries = Queries(
    getTodo = args => TodoItemPersistenceService.getTodoItem(args.id)
  )

  implicit val queriesSchema = gen[Queries]

  val api: GraphQL[Console with Clock with TaskPersistence] =
    graphQL(
      RootResolver(
        queries
      )
    ).withWrapper(timeout(3.seconds))
      .withWrapper(printSlowQueries(500.milliseconds))
}
