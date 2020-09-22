package dev.nhyne.numberz

import zio.random.nextIntBounded
import zio.console.{getStrLn, putStrLn, Console}
import zio.{App, ExitCode, IO, ZIO}
import zio._
import java.io.IOException

object Numberz extends App {
  case class State(number: Int, currentGuess: Int, guessNumber: Int) {
    final def correct: Boolean = number == currentGuess
  }
  def run(args: List[String]) = //: ZIO[ZEnv, Nothing, Int] =
    game.foldM(
      _ => UIO.succeed(ExitCode.success),
      _ => UIO.succeed(ExitCode.failure)
    )

  val getGuess: ZIO[Console, IOException, Int] = for {
    _ <- putStrLn("What's your guess?")
    numString <- getStrLn
  } yield numString.toInt

  val pickNumber = nextIntBounded(100)

  val game = for {
    number <- pickNumber
    state = State(number = number, currentGuess = 0, guessNumber = 0)
    _ <- gameLoop(state)
  } yield ()

  def gameLoop(state: State): ZIO[Console, IOException, State] =
    for {
      _ <- putStrLn(s"${state.number}")
      guess <- getGuess
      newState = State(state.number, guess, state.guessNumber + 1)
      _ <-
        if (newState.correct) putStrLn("You guessed correctly!!")
        else putStrLn("Wrong, guess again")
      state <-
        if (newState.correct) IO.succeed(newState)
        else gameLoop(newState)

    } yield state
}
