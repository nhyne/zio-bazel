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
        "Configuration.scala",
    ] + glob(include = [
        "domain/*.scala",
        "parser/*.scala",
        "persistence/*.scala",
    ]),
    resources = [
        "application.conf",
    ],
    deps = [
        "@scala_2_12//:com_chuusai_shapeless_2_12",
        "@scala_2_12//:com_github_pureconfig_pureconfig_core_2_12",
        "@scala_2_12//:com_github_pureconfig_pureconfig_generic_2_12",
        "@scala_2_12//:com_lihaoyi_sourcecode_2_12",
        "@scala_2_12//:dev_zio_izumi_reflect_2_12",
        "@scala_2_12//:dev_zio_zio_2_12",
        "@scala_2_12//:dev_zio_zio_interop_cats_2_12",
        "@scala_2_12//:dev_zio_zio_query_2_12",
        "@scala_2_12//:org_tpolecat_doobie_core_2_12",
        "@scala_2_12//:org_tpolecat_doobie_free_2_12",
        "@scala_2_12//:org_typelevel_cats_core_2_12",
        "@scala_2_12//:org_typelevel_cats_effect_2_12",
        "@scala_2_12//:org_typelevel_cats_free_2_12",
    ],
)

scala_format_test(
    name = "format",
    size = "small",
    srcs = glob(include = ["**/*.scala"]),
)
