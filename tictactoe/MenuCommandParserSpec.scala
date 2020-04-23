package dev.nhyne.tictactoe.parser

import dev.nhyne.tictactoe.domain.MenuCommand
import zio.test.{assertM, DefaultRunnableSpec, suite, testM, checkM, Gen}
import zio.test.Assertion.equalTo
import MenuCommandParserSpecUtils._

object MenuCommandParserSpec extends DefaultRunnableSpec {
  def spec = suite("MenuCommandParser")(
    suite("pase")(testM("'new game' - return NewGame command") {
      checkParse("new game", MenuCommand.NewGame)
    }, testM("'quit' - return Quit command") {
      checkParse("quit", MenuCommand.Quit)
    }, testM("'resume' - return Resume command") {
      checkParse("resume", MenuCommand.Resume)
    }, testM("'xxx' - return Invalid command") {
      checkM(invalidCommandsGen) { input =>
        checkParse(input, MenuCommand.Invalid)
      }
    })
  )
}

object MenuCommandParserSpecUtils {
  val validCommands =
    List("new game", "resume", "quit")
  val invalidCommandsGen =
    Gen.anyString.filter(str => !validCommands.contains(str))
  def checkParse(input: String, command: MenuCommand) = {
    val app = MenuCommandParser.>.parse(input)
    val env = new MenuCommandParserLive {}
    val result = app.provide(env)
    assertM(result)(equalTo(command))
  }
}
