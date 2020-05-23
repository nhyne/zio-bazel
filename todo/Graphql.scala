package dev.nhyne.todo

import dev.nhyne.todo.domain.TodoItem
import dev.nhyne.todo.persistence.TodoItemPersistenceService
import dev.nhyne.todo.persistence.TodoItemPersistenceService.TaskPersistence
import zio.RIO
import caliban.GraphQL.graphQL
import caliban.RootResolver
import caliban.schema.GenericSchema
import caliban.GraphQL.graphQL
import scala.language.higherKinds

object Graphql extends GenericSchema[TaskPersistence] {
  case class GetTodoArgs(id: Int)

  case class Queries(
      getTodo: GetTodoArgs => RIO[TaskPersistence, TodoItem]
  )

  val queries = Queries(
    getTodo = args => TodoItemPersistenceService.getTodoItem(args.id)
  )

  implicit val queriesSchema = gen[Queries]

  val api = graphQL(RootResolver(queries))
}
