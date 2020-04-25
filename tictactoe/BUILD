load(
    "@rules_scala_annex//rules:scala.bzl",
    "scala_binary",
    "scala_test",
)

scala_binary(
    name = "tictactoe",
    srcs = [
        "Main.scala",
        "cli/Terminal.scala",
        "cli/TerminalLive.scala",
        "parser/MenuCommandParser.scala",
        "parser/MenuCommandParserLive.scala",
    ] + glob(include = [
        "domain/*.scala",
        "app/*.scala",
    ]),
    deps = [
        "@scala_2_12//:com_beachape_enumeratum_2_12",
        "@scala_2_12//:dev_zio_zio_2_12",
    ],
)

scala_test(
    name = "test",
    size = "small",
    srcs = [
        "parser/MenuCommandParserSpec.scala",
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
