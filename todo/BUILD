load(
    "@rules_scala_annex//rules:scala.bzl",
    "scala_binary",
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
    name = "todo",
    srcs = [
        "Main.scala",
        "ApiService.scala",
        "GraphqlService.scala",
    ] + glob(include = [
        "domain/*.scala",
        "configuration/*.scala",
        "persistence/*.scala",
    ]),
    resource_strip_prefix = package_name(),
    resources = [
        "application.conf",
    ],
    scalacopts = scala_options,
    runtime_deps = [
        "@scala_2_12//:org_tpolecat_doobie_postgres_2_12",
    ],
    deps = [
        "@scala_2_12//:com_chuusai_shapeless_2_12",
        "@scala_2_12//:com_github_ghostdogpr_caliban_2_12",
        "@scala_2_12//:com_github_ghostdogpr_caliban_http4s_2_12",
        "@scala_2_12//:com_github_pureconfig_pureconfig_core_2_12",
        "@scala_2_12//:com_github_pureconfig_pureconfig_generic_2_12",
        "@scala_2_12//:com_lihaoyi_sourcecode_2_12",
        "@scala_2_12//:com_propensive_magnolia_2_12",
        "@scala_2_12//:com_propensive_mercator_2_12",
        "@scala_2_12//:com_zaxxer_HikariCP",
        "@scala_2_12//:dev_zio_izumi_reflect_2_12",
        "@scala_2_12//:dev_zio_zio_2_12",
        "@scala_2_12//:dev_zio_zio_interop_cats_2_12",
        #        "@scala_2_12//:dev_zio_zio_logging_2_12",
        "@scala_2_12//:io_circe_circe_core_2_12",
        "@scala_2_12//:io_circe_circe_generic_2_12",
        "@scala_2_12//:org_http4s_http4s_blaze_server_2_12",
        "@scala_2_12//:org_http4s_http4s_circe_2_12",
        "@scala_2_12//:org_http4s_http4s_core_2_12",
        "@scala_2_12//:org_http4s_http4s_dsl_2_12",
        "@scala_2_12//:org_http4s_http4s_server_2_12",
        "@scala_2_12//:org_tpolecat_doobie_core_2_12",
        "@scala_2_12//:org_tpolecat_doobie_free_2_12",
        "@scala_2_12//:org_tpolecat_doobie_hikari_2_12",
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
