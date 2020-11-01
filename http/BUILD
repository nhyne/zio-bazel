load(
    "@rules_scala_annex//rules:scala.bzl",
    "scala_binary",
    "scala_test",
)

scala_binary(
    name = "http",
    srcs = [
        "Main.scala",
    ],
    scala = "//scala:zinc_2_13_1",
    deps = [
        "@scala_2_13//:dev_zio_zio_2_13",
    ],
)
