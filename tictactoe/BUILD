load(
    "@rules_scala_annex//rules:scala.bzl",
    "scala_binary",
    "scala_test",
    "scala_repl",
)

scala_binary(
    name = "tictactoe",
    srcs = [
        "Main.scala",
    ] + glob(
        include = [
            "app/*.scala",
            "cli/*.scala",
            "domain/*.scala",
            "logic/*.scala",
            "mode/*.scala",
            "parser/*.scala",
            "view/*.scala",
        ],
        exclude = ["parser/MenuCommandParserSpec.scala"],
    ),
    deps = [
        "@scala_2_12//:com_beachape_enumeratum_2_12",
        "@scala_2_12//:dev_zio_zio_2_12",
        "@scala_2_12//:org_tpolecat_atto_core_2_12",
    ],
)

scala_repl(
    name = "repl",
    deps = ["tictactoe"]
)

scala_test(
    name = "test",
    size = "small",
    srcs = [
        "parser/MenuCommandParser.scala",
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

