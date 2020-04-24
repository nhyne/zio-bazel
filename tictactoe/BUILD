load(
    "@rules_scala_annex//rules:scala.bzl",
    "scala_binary",
    "scala_test",
)

scala_binary(
    name = "tictactoe",
    srcs = [
        "Main.scala",
        "MenuCommand.scala",
        "MenuCommandParser.scala",
        "MenuCommandParserLive.scala",
        "Terminal.scala",
        "TerminalLive.scala",
    ],
    deps = [
        "@scala_2_12//:dev_zio_zio_2_12",
    ],
)

scala_test(
    name = "test",
    size = "small",
    srcs = [
        "MenuCommandParserSpec.scala",
    ],
    frameworks = [
        "zio.test.sbt.ZTestFramework",
    ],
    runtime_deps = [
        "@scala_2_12//:dev_zio_zio_test_sbt_2_12",
    ],
    deps = [
        "tictactoe",
        "@scala_2_12//:dev_zio_zio_2_12",
        "@scala_2_12//:dev_zio_zio_test_2_12",
    ],
)
