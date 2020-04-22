import zio.{App, URIO, ZIO, console}
import zio.console.Console

object TicTacToe extends App {
  val program : ZIO[Console, Nothing, Unit] = for {
        _ <- console.putStrLn("Hello World!")
    } yield {}

    def run(args: List[String]): URIO[Console, Int] =
    program.as(0)
}
