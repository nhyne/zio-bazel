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
    name = "todo_lib",
    srcs = [
        "Main.scala",
        "GraphqlService.scala",
    ] + glob(include = [
        "domain/*.scala",
        "configuration/*.scala",
        "persistence/*.scala",
    ]),
    resource_strip_prefix = package_name(),
    scala = "//scala:zinc_2_13_1",
    scalacopts = scala_options,
    runtime_deps = [
        "@scala_2_13//:org_tpolecat_doobie_postgres_2_13",
    ],
    deps = [
        "@scala_2_13//:com_chuusai_shapeless_2_13",
        "@scala_2_13//:com_github_ghostdogpr_caliban_2_13",
        "@scala_2_13//:com_github_ghostdogpr_caliban_http4s_2_13",
        "@scala_2_13//:com_github_pureconfig_pureconfig_core_2_13",
        "@scala_2_13//:com_github_pureconfig_pureconfig_generic_2_13",
        "@scala_2_13//:com_lihaoyi_sourcecode_2_13",
        "@scala_2_13//:com_propensive_magnolia_2_13",
        "@scala_2_13//:com_propensive_mercator_2_13",
        "@scala_2_13//:com_zaxxer_HikariCP",
        "@scala_2_13//:dev_zio_izumi_reflect_2_13",
        "@scala_2_13//:dev_zio_zio_2_13",
        "@scala_2_13//:dev_zio_zio_interop_cats_2_13",
        "@scala_2_13//:dev_zio_zio_query_2_13",
        "@scala_2_13//:io_scalaland_chimney_2_13",
        "@scala_2_13//:org_http4s_http4s_blaze_server_2_13",
        "@scala_2_13//:org_http4s_http4s_core_2_13",
        "@scala_2_13//:org_http4s_http4s_server_2_13",
        "@scala_2_13//:org_tpolecat_doobie_core_2_13",
        "@scala_2_13//:org_tpolecat_doobie_free_2_13",
        "@scala_2_13//:org_tpolecat_doobie_hikari_2_13",
        "@scala_2_13//:org_typelevel_cats_core_2_13",
        "@scala_2_13//:org_typelevel_cats_effect_2_13",
        "@scala_2_13//:org_typelevel_cats_free_2_13",
    ],
)

scala_binary(
    name = "todo",
    main_class = "dev.nhyne.todo.Main",
    resource_strip_prefix = package_name(),
    resources = [
        "application.conf",
        "graphiql.html",
    ],
    scala = "//scala:zinc_2_13_1",
    scalacopts = scala_options,
    runtime_deps = [
        "todo_lib",
        "@scala_2_13//:org_tpolecat_doobie_postgres_2_13",
    ],
)

scala_repl(
    name = "repl",
    scala = "//scala:zinc_2_13_1",
    deps = [
        "todo",
    ],
)

scala_format_test(
    name = "format",
    size = "small",
    srcs = glob(include = ["**/*.scala"]),
)
