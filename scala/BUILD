load(
    "@rules_scala_annex//rules:scala.bzl",
    "configure_bootstrap_scala",
    "configure_zinc_scala",
    "scala_library",
)

compiler_classpath_2_13_1 = [
    "@scala_2_13//:org_scala_lang_scala_compiler",
    "@scala_2_13//:org_scala_lang_scala_library",
    "@scala_2_13//:org_scala_lang_scala_reflect",
]

# This creates a basic ScalaConfiguration that relies on the scalac compiler
configure_bootstrap_scala(
    name = "bootstrap_2_13_1",
    compiler_classpath = compiler_classpath_2_13_1,
    runtime_classpath = compiler_classpath_2_13_1,
    version = "2.13.1",
    visibility = ["//visibility:public"],
)

# This augments the configuration to configure the zinc compiler
configure_zinc_scala(
    name = "zinc_2_13_1",
    compiler_bridge = "@scala_2_13//:org_scala_sbt_compiler_bridge_2_13",
    compiler_classpath = compiler_classpath_2_13_1,
    runtime_classpath = compiler_classpath_2_13_1,
    version = "2.13.1",
    visibility = ["//visibility:public"],
)
