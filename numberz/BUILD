load(
    "@rules_scala_annex//rules:scala.bzl",
    "scala_binary",
    "scala_test",
)

scala_binary(
    name = "numberz",
    srcs = [
        "Main.scala",
    ],
    deps = [
        "@scala_2_12//:dev_zio_zio_2_12",
    ],
)
