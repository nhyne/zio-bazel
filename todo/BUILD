load(
    "@rules_scala_annex//rules:scala.bzl",
    "scala_binary",
    "scala_test",
)
load("@rules_scala_annex//rules:scalafmt.bzl", "scala_format_test")

scala_binary(
    name = "todo",
    srcs = [
        "Main.scala",
    ] + glob(include = [
        "domain/*.scala",
        "parser/*.scala",
    ]),
    deps = [
        "@scala_2_12//:dev_zio_izumi_reflect_2_12",
        "@scala_2_12//:dev_zio_zio_2_12",
    ],
)

scala_format_test(
    name = "format",
    size = "small",
    srcs = glob(include = ["**/*.scala"]),
)
