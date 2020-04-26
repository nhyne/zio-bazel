package dev.nhyne.numberz

import java.io.IOException

import zio.console.{Console, getStrLn, putStrLn}
import zio.{App, ZEnv, ZIO, IO}
import zio._

object Numberz extends App {
  case class State(number: Int, currentGuess: Int) {
    final def guess: Boolean = number == currentGuess
  }
  def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = ???

  val getGuess
    : ZIO[Console, IOException, String] = putStrLn("something") *> getStrLn
}
