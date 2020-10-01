package dev.nhyne.parallel

import zio.test._

object MainSpec extends DefaultRunnableSpec {

  def spec =
    suite("some test spec") {
      test("some test")(assert("some value")(Assertion.isNonEmptyString))
    }
}
