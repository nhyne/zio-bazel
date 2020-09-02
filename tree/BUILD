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
    name = "tree",
    srcs = [
        "Main.scala",
    ],
    main_class = "dev.nhyne.tree.Main",
    scala = "//scala:zinc_2_13_1",
    scalacopts = scala_options,
)
