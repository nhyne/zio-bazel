load("@rules_scala_annex//rules:scalafmt.bzl", "scala_format_test")
load(
    "@rules_scala_annex//rules:scala.bzl",
    "scala_binary",
    "scala_library",
    "scala_repl",
    "scala_test",
)

scalacopts = [
    "-Yrangepos",
    "-Xfatal-warnings",
    "-Xlint",
    "-Yno-adapted-args",
    "-Ypartial-unification",
    "-Ywarn-extra-implicit",
    "-Ywarn-inaccessible",
    "-Ywarn-infer-any",
    "-Ywarn-nullary-override",
    "-Ywarn-nullary-unit",
    "-Ywarn-numeric-widen",
    "-Ywarn-unused:patvars,-implicits",
    "-Ywarn-value-discard",
    "-opt-warnings",
]

scala_binary(
    name = "src",
    srcs = [
        "main.scala",
    ],
    # this removes the folder prefix (graphql/) from the resources so that
    # play can find application.conf at the top-level
    #    resource_strip_prefix = package_name(),
    #    resources = [
    #        "application.conf",
    #        "logback.xml",
    #        "workbench.html",
    #    ],
#        scala = "//toolchains:zinc_2_12_10",
#scala = "@default_scala",
    scalacopts = scalacopts,
    deps = [
#                "@scala_2_12//:dev_zio_izumi_reflect_2_12",
                "@scala_2_12//:dev_zio_zio_2_12",
        #        "@scala_2_12//:dev_zio_zio_config_2_12",
        #        "@scala_2_12//:dev_zio_zio_config_magnolia_2_12",
        #        "@scala_2_12//:dev_zio_zio_config_typesafe_2_12",
        #        "@scala_2_12//:dev_zio_zio_interop_cats_2_12",
        #        "@scala_2_12//:dev_zio_zio_logging_2_12",
        #        "@scala_2_12//:dev_zio_zio_logging_slf4j_2_12",
        #        "@scala_2_12//:dev_zio_zio_metrics_prometheus_2_12",
    ],
)

scala_format_test(
    name = "format",
    size = "small",
    srcs = glob(
        ["*.scala"],
    ),
)
