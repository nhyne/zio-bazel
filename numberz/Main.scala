package dev.nhyne.numberz

import java.io.IOException

import zio.random.{nextIntBounded, Random}
import zio.console.{Console, getStrLn, putStrLn}
import zio.{App, ZEnv, ZIO, IO}
import zio._

object Numberz extends App {
  case class State(number: Int, currentGuess: Int, guessNumber: Int) {
    final def correct: Boolean = number == currentGuess
  }
  def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = {
        game.foldM(
            _ => UIO.succeed(1),
                _ => UIO.succeed(0)
        )
  }

  val getGuess
    : ZIO[Console, IOException, Int] = for {
      _ <- putStrLn("What's your guess?")
      numString <- getStrLn
  } yield {
      numString.toInt
  }

    val pickNumber = nextIntBounded(100)

    val game = for {
        number <- pickNumber
        state = State(number = number, currentGuess = 0, guessNumber = 0)
        _ <- gameLoop(state)
    } yield ()

    def gameLoop(state: State): ZIO[Console, IOException, State] = for {
        _ <- putStrLn(s"${state.number}")
        guess <- getGuess
        newState = State(state.number, guess, state.guessNumber + 1)
        _ <- if (newState.correct) putStrLn("You guessed correctly!!")
        else putStrLn("Wrong, guess again")
        state <- if(newState.correct) IO.succeed(newState) else gameLoop(newState)

    } yield state
}
