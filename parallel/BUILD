load(
    "@rules_scala_annex//rules:scala.bzl",
    "scala_binary",
    "scala_library",
    "scala_repl",
    "scala_test",
)
load("@rules_scala_annex//rules:scalafmt.bzl", "scala_format_test")

scala_options = [
    "-Xlint",
    "-Ywarn-extra-implicit",
    "-Ywarn-unused:patvars,-implicits",
    "-Ywarn-value-discard",
    "-opt-warnings",
    "-deprecation",
    "-feature",
    "-Xfatal-warnings",
]

scala_binary(
    name = "parallel",
    srcs = [
        "Main.scala",
        "package.scala",
    ],
    resource_strip_prefix = package_name(),
    scala = "//scala:zinc_2_13_1",
    scalacopts = scala_options,
    deps = [
        "@scala_2_13//:dev_zio_izumi_reflect_2_13",
        "@scala_2_13//:dev_zio_zio_2_13",
    ],
)

scala_test(
    name = "parallel_test",
    size = "small",
    srcs = [
        "Main.scala",
        "MainSpec.scala",
        "package.scala",
    ],
    frameworks = [
        "zio.test.sbt.ZTestFramework",
    ],
    scala = "//scala:zinc_2_13_1",
    scalacopts = scala_options,
    runtime_deps = [
        "@scala_2_13//:dev_zio_zio_test_sbt_2_13",
    ],
    deps = [
        "@scala_2_13//:dev_zio_zio_2_13",
        "@scala_2_13//:dev_zio_zio_test_2_13",
    ],
)

scala_repl(
    name = "repl",
    scala = "//scala:zinc_2_13_1",
    deps = [
        "parallel",
    ],
)

scala_format_test(
    name = "format",
    size = "small",
    srcs = glob(include = ["**/*.scala"]),
)
