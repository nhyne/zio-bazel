package dev.nhyne.todo

import dev.nhyne.todo.domain.{TodoItem, UninsertedTodoItem}
import dev.nhyne.todo.persistence.TodoItemPersistenceService
import dev.nhyne.todo.persistence.TodoItemPersistenceService.{
  createTodoItem,
  deleteTodoItem,
  getTodoItem
}
import zio.RIO
import org.http4s.circe._
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes}
import org.http4s.dsl.Http4sDsl
import io.circe.{Decoder, Encoder}
import io.circe.generic.auto._
import persistence.TodoItemPersistenceService._
import zio.interop.catz._

final case class ApiService[R <: TodoItemPersistenceService.TaskPersistence](
    rootUri: String
) {
  type TodoTask[A] = RIO[R, A]

  implicit def circeJsonDecoder[A](
      implicit decoder: Decoder[A]
  ): EntityDecoder[TodoTask, A] = jsonOf[TodoTask, A]
  implicit def circeJsonEncoder[A](
      implicit decoder: Encoder[A]
  ): EntityEncoder[TodoTask, A] = jsonEncoderOf[TodoTask, A]

  val dsl = Http4sDsl[TodoTask]
  import dsl._

  def route: HttpRoutes[TodoTask] = {
    HttpRoutes.of[TodoTask] {
      case GET -> Root / IntVar(id) =>
        getTodoItem(id).foldM(_ => NotFound(), Ok(_))
      case DELETE -> Root / IntVar(id) =>
        deleteTodoItem(id).foldM(_ => NotFound(), Ok(_))
      case request @ POST -> Root =>
        request.decode[UninsertedTodoItem] { todo =>
          Created(createTodoItem(todo))
        }
    }
  }
}
