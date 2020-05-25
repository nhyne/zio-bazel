package dev.nhyne.todo

import dev.nhyne.todo.persistence.{
  CalibanTodoList,
  TodoItem,
  TodoItemPersistenceService,
  TodoList,
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
import zio.query.{DataSource, Request, ZQuery}

object GraphqlService
    extends GenericSchema[TaskPersistence with TodoPersistence] {
  case class GetTodoItemArgs(todoId: Int)
  case class GetTodoListArgs(todoListId: Int)
  case class GetTodoListsArgs(limit: Int)
  case class GetTodosForListArgs(listId: Int)

  case class Queries(
      getTodo: GetTodoItemArgs => RIO[TaskPersistence, TodoItem],
      getTodosForList: GetTodosForListArgs => RIO[TaskPersistence, List[
        TodoItem
      ]],
      getTodoList: GetTodoListArgs => ZQuery[
        TodoPersistence,
        Throwable,
        TodoList
      ],
      getTodoLists: GetTodoListsArgs => RIO[TodoPersistence, List[TodoList]]
  )

  def resolver(todoListService: TodoListPersistenceService) = {

    case class GetTodoList(id: Int) extends Request[Nothing, TodoList]
    val TodoListDataSource: DataSource[Any, GetTodoList] =
      DataSource.fromFunctionM("TodoListDataSource")(req =>
        todoListService.get(req.id)
      )
    def getTodoList(id: Int): ZQuery[Any, Nothing, TodoList] =
      ZQuery.fromRequest(GetTodoList(id))(TodoListDataSource)

    Queries(
      getTodo = args => TodoItemPersistenceService.getTodoItem(args.todoId),
      getTodosForList =
        args => TodoItemPersistenceService.getTodosForList(args.listId),
      getTodoList = args => getTodoList(args.todoListId),
      getTodoLists = args => TodoListPersistenceService.getTodoLists(args.limit)
    )
  }
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
