load(
    "@rules_scala_annex//rules:scala.bzl",
    "scala_binary",
    "scala_test",
)

scala_binary(
    name = "todo",
    srcs = [
        "Main.scala",
    ] + glob(include = [
        "domain/*.scala",
#                "mode/*.scala",
        "parser/*.scala",
    ]),
    deps = [
        "@scala_2_12//:dev_zio_izumi_reflect_2_12",
        "@scala_2_12//:dev_zio_zio_2_12",
    ],
)
