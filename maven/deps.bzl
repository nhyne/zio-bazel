load("@rules_jvm_external//:defs.bzl", "maven_install")
load("@rules_jvm_external//:specs.bzl", "maven")
load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_file")

zio_version = "1.0.3"
circe_version = "0.13.0"
caliban_version = "0.9.1"
http4s_version = "0.21.8"
doobie_version = "0.8.8"

scala_2_13 = "2.13.3"

def install_maven_deps():
    maven_install(
        name = "scala_2_12",
        artifacts = [
            "dev.zio:zio-test_2.12:{zio_version}".format(zio_version = zio_version),
            "dev.zio:zio-test-sbt_2.12:{zio_version}".format(zio_version = zio_version),
        ],
        repositories = [
            "https://maven.google.com",
            "https://repo1.maven.org/maven2",
        ],
        fetch_sources = True,
        version_conflict_policy = "pinned",
        use_unsafe_shared_cache = False,
        maven_install_json = "//maven:scala_2_12_install.json",
    )

    maven_install(
        name = "scala_2_13",
        artifacts = [
            "dev.zio:zio-test_2.13:{zio_version}".format(zio_version = zio_version),
            "dev.zio:zio-test-sbt_2.13:{zio_version}".format(zio_version = zio_version),
            "dev.zio:zio-interop-cats_2.13:2.2.0.1",
            "dev.zio:zio-logging_2.13:0.4.0",
            "org.tpolecat:doobie-core_2.13:{doobie_version}".format(doobie_version = doobie_version),
            "org.tpolecat:doobie-postgres_2.13:{doobie_version}".format(doobie_version = doobie_version),
            "org.tpolecat:doobie-hikari_2.13:{doobie_version}".format(doobie_version = doobie_version),
            "org.http4s:http4s-blaze-server_2.13:{http4s_version}".format(http4s_version = http4s_version),
            "org.http4s:http4s-circe_2.13:{http4s_version}".format(http4s_version = http4s_version),
            "org.http4s:http4s-dsl_2.13:{http4s_version}".format(http4s_version = http4s_version),
            "com.github.pureconfig:pureconfig_2.13:0.12.3",
            "io.scalaland:chimney_2.13:0.5.2",
            "com.github.ghostdogpr:caliban_2.13:{caliban_version}".format(caliban_version = caliban_version),
            "com.github.ghostdogpr:caliban-http4s_2.13:{caliban_version}".format(caliban_version = caliban_version),
            "io.circe:circe-core_2.13:{circe_version}".format(circe_version = circe_version),
            "io.circe:circe-generic_2.13:{circe_version}".format(circe_version = circe_version),
            "io.circe:circe-parser_2.13:{circe_version}".format(circe_version = circe_version),
            "org.scala-lang:scala-compiler:{scala_2_13}".format(scala_2_13 = scala_2_13),
            "org.scala-lang:scala-library:{scala_2_13}".format(scala_2_13 = scala_2_13),
            "org.scala-lang:scala-reflect:{scala_2_13}".format(scala_2_13 = scala_2_13),
            "org.scala-sbt:compiler-bridge_2.13:1.3.4",
            #            "io.tryp:splain_2.12.10:0.5.1",
        ],
        repositories = [
            "https://maven.google.com",
            "https://repo1.maven.org/maven2",
        ],
        fetch_sources = True,
        version_conflict_policy = "pinned",
        use_unsafe_shared_cache = False,
        maven_install_json = "//maven:scala_2_13_install.json",
    )
