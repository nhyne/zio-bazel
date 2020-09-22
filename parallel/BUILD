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

scala_library(
    name = "parallel_lib",
    srcs = [
        "Main.scala",
    ],
    resource_strip_prefix = package_name(),
    scala = "//scala:zinc_2_13_1",
    scalacopts = scala_options,
    deps = [
        "@scala_2_13//:dev_zio_zio_2_13",
    ],
)

scala_binary(
    name = "parallel",
    main_class = "dev.nhyne.parallel.Main",
    resource_strip_prefix = package_name(),
    scala = "//scala:zinc_2_13_1",
    scalacopts = scala_options,
    runtime_deps = [
        "parallel_lib",
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
