package dev.nhyne.todo

import dev.nhyne.todo.persistence.{
  CalibanTodoList,
  Note,
  NotePersistenceService,
  TodoItem,
  TodoItemPersistenceService,
  TodoList,
  TodoListPersistenceService,
  UninsertedNote,
  UninsertedTodoItem,
  UninsertedTodoList
}
import dev.nhyne.todo.persistence.TodoItemPersistenceService.TaskPersistence
import zio.RIO
import caliban.wrappers.Wrappers.{printSlowQueries, timeout}
import zio.duration._
import caliban.{GraphQL, RootResolver}
import caliban.schema.GenericSchema
import caliban.GraphQL.graphQL
import dev.nhyne.todo.persistence.NotePersistenceService.NotePersistence
import dev.nhyne.todo.persistence.TodoListPersistenceService.TodoPersistence
import zio.clock.Clock
import zio.console.Console
import zio.random.Random

object GraphqlService
    extends GenericSchema[
      TaskPersistence with TodoPersistence with NotePersistence with Random
    ] {
  case class GetTodoItemArgs(todoId: Int)
  case class GetTodoListArgs(todoListId: Int)
  case class GetTodoListsArgs(limit: Int)
  case class GetTodosForListArgs(listId: Int)
  case class GetNoteArgs(noteId: Int)
  case class GetRandomNoteArgs()

  case class CreateTodoList(name: String)
  case class CreateTodoItem(
    listId: Int,
    title: String,
    description: Option[String],
    completed: Boolean
  )
  case class MarkTodoItemComplete(id: Int)
  case class CreateNote(
    title: String,
    contents: String
  )

  case class Queries(
    getTodo: GetTodoItemArgs => RIO[TaskPersistence, TodoItem],
    getTodosForList: GetTodosForListArgs => RIO[TaskPersistence, List[
      TodoItem
    ]],
    getTodoList: GetTodoListArgs => RIO[TodoPersistence, CalibanTodoList],
    getTodoLists: GetTodoListsArgs => RIO[TodoPersistence, List[TodoList]],
    getNote: GetNoteArgs => RIO[NotePersistence, Note],
    getRandomNote: GetRandomNoteArgs => RIO[NotePersistence with Random, Note]
  )

  case class Mutations(
    createTodoList: CreateTodoList => RIO[TodoPersistence, TodoList],
    createTodoItem: CreateTodoItem => RIO[TaskPersistence, TodoItem],
    markTodoItemComplete: MarkTodoItemComplete => RIO[
      TaskPersistence,
      TodoItem
    ],
    createNote: CreateNote => RIO[NotePersistence, Note]
  )

  val queries = Queries(
    getTodo = args => TodoItemPersistenceService.getTodoItem(args.todoId),
    getTodosForList =
      args => TodoItemPersistenceService.getTodosForList(args.listId),
    getTodoList =
      args => TodoListPersistenceService.getTodoList(args.todoListId),
    getTodoLists = args => TodoListPersistenceService.getTodoLists(args.limit),
    getNote = args => NotePersistenceService.getNote(args.noteId),
    getRandomNote = _ => NotePersistenceService.getRandom()
  )

  val mutations = Mutations(
    createTodoList = args =>
      TodoListPersistenceService.createTodoList(UninsertedTodoList(args.name)),
    createTodoItem = args =>
      TodoItemPersistenceService.createTodoItem(
        UninsertedTodoItem(
          title = args.title,
          description = args.description,
          completed = args.completed,
          listId = args.listId
        )
      ),
    markTodoItemComplete =
      args => TodoItemPersistenceService.markTodoItemComplete(args.id),
    createNote = args =>
      NotePersistenceService.createNote(
        UninsertedNote(args.title, args.contents)
      )
  )

  implicit val queriesSchema = gen[Queries]

  val api: GraphQL[
    Console with Clock with Random with TaskPersistence with TodoPersistence with NotePersistence
  ] =
    graphQL(
      RootResolver(
        queries,
        mutations
      )
    ).withWrapper(timeout(3.seconds))
      .withWrapper(printSlowQueries(500.milliseconds))
}
