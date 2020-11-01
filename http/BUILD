load(
    "@rules_scala_annex//rules:scala.bzl",
    "scala_binary",
    "scala_test",
)

scala_binary(
    name = "http",
    srcs = [
        "HttpTestService.scala",
        "Main.scala",
    ],
    scala = "//scala:zinc_2_13_1",
    deps = [
        "@scala_2_13//:dev_zio_izumi_reflect_2_13",
        "@scala_2_13//:dev_zio_zio_2_13",
    ],
)
