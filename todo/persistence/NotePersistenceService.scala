package dev.nhyne.todo.persistence

import dev.nhyne.todo.configuration.Configuration.Configuration
import dev.nhyne.todo.configuration.{Configuration, DbConfig}
import doobie.{Query0, Transactor, Update0}
import zio._
import doobie.implicits._
import zio.interop.catz._
import zio.blocking.Blocking

import scala.concurrent.ExecutionContext

final case class UninsertedNote(
  title: String,
  content: String
)

final case class Note(
  id: Int,
  title: String,
  content: String
)

final case class NoteNotFound(id: Int) extends Exception

final class NotePersistenceService(tnx: Transactor[Task])
    extends NotePersistenceService.Service {
  import NotePersistenceService._

  def get(id: Int): Task[Note] =
    SQL
      .get(id)
      .option
      .transact(tnx)
      .foldM(
        err => Task.fail(err),
        maybeNote => Task.require(NoteNotFound(id))(Task.succeed(maybeNote))
      )

  def create(note: UninsertedNote): Task[Note] =
    SQL
      .create(note)
      .withUniqueGeneratedKeys[Note](
        "id",
        "title",
        "content"
      )
      .transact(tnx)
      .foldM(
        err => {
          println(s"Error: $err")
          Task.fail(err)
        },
        note => Task.succeed(note)
      )

  def delete(id: Int): Task[Boolean] =
    SQL
      .delete(id)
      .run
      .transact(tnx)
      .fold(
        _ => false,
        _ => true
      )
}

object NotePersistenceService {

  trait Service {
    def get(id: Int): ZIO[NotePersistence, Throwable, Note]
    def create(
      note: UninsertedNote
    ): ZIO[NotePersistence, Throwable, Note]
    def delete(id: Int): ZIO[NotePersistence, Throwable, Boolean]
  }

  type NotePersistence = Has[Service]

  val live: ZLayer[
    Configuration with Blocking,
    Throwable,
    NotePersistence
  ] =
    ZLayer.fromManaged(
      for {
        config <- Configuration.load.toManaged_
        connectEC <- ZIO.descriptor.map(_.executor.asEC).toManaged_
        managed <- mkTransactor(config.dbConfig, connectEC)
      } yield managed
    )

  def getNote(id: Int): RIO[NotePersistence, Note] =
    RIO.accessM[NotePersistence](_.get.get(id))

  def createNote(note: UninsertedNote): RIO[NotePersistence, Note] =
    RIO.accessM[NotePersistence](_.get.create(note))

  def deleteNote(id: Int): RIO[NotePersistence, Boolean] =
    RIO.accessM[NotePersistence](_.get.delete(id))

  object SQL {
    def get(id: Int): Query0[Note] =
      sql"""SELECT * FROM NOTES WHERE ID = $id""".query[Note]

    def create(note: UninsertedNote): Update0 =
      sql"""INSERT INTO NOTES (title, content) VALUES (${note.title}, ${note.content})""".update

    def delete(id: Int): Update0 =
      sql"""DELETE FROM NOTES WHERE id = $id""".update
  }

  def mkTransactor(
    conf: DbConfig,
    connectEC: ExecutionContext
  ): Managed[Throwable, NotePersistenceService] =
    mkBaseTransactor(conf, connectEC).toManagedZIO
      .map(new NotePersistenceService(_))

}
