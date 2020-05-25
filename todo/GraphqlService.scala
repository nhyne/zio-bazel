package dev.nhyne.todo

import dev.nhyne.todo.persistence.{
  CalibanTodoList,
  TodoItem,
  TodoItemPersistenceService,
  TodoList,
  TodoListPersistenceService,
  UninsertedTodoList
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
  case class GetTodoListsArgs(limit: Int)
  case class GetTodosForListArgs(listId: Int)

  case class CreateTodoList(name: String)

  case class Queries(
      getTodo: GetTodoItemArgs => RIO[TaskPersistence, TodoItem],
      getTodosForList: GetTodosForListArgs => RIO[TaskPersistence, List[
        TodoItem
      ]],
      getTodoList: GetTodoListArgs => RIO[TodoPersistence, CalibanTodoList],
      getTodoLists: GetTodoListsArgs => RIO[TodoPersistence, List[TodoList]]
  )

  case class Mutations(
      createTodoList: CreateTodoList => RIO[TodoPersistence, TodoList]
  )

  val queries = Queries(
    getTodo = args => TodoItemPersistenceService.getTodoItem(args.todoId),
    getTodosForList =
      args => TodoItemPersistenceService.getTodosForList(args.listId),
    getTodoList =
      args => TodoListPersistenceService.getTodoList(args.todoListId),
    getTodoLists = args => TodoListPersistenceService.getTodoLists(args.limit)
  )

  val mutations = Mutations(
    createTodoList = args =>
      TodoListPersistenceService.createTodoList(UninsertedTodoList(args.name))
  )

  implicit val queriesSchema = gen[Queries]

  val api
      : GraphQL[Console with Clock with TaskPersistence with TodoPersistence] =
    graphQL(
      RootResolver(
        queries,
        mutations
      )
    ).withWrapper(timeout(3.seconds))
      .withWrapper(printSlowQueries(500.milliseconds))
}
