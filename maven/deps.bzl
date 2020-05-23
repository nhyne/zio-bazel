load("@rules_jvm_external//:defs.bzl", "maven_install")
load("@rules_jvm_external//:specs.bzl", "maven")
load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_file")

zio_version = "1.0.0-RC18-2"
circe_version = "0.13.0"
caliban_version = "0.8.0"

def install_maven_deps():
    maven_install(
        name = "scala_2_12",
        artifacts = [
            "dev.zio:zio-test_2.12:{zio_version}".format(zio_version = zio_version),
            "dev.zio:zio-test-sbt_2.12:{zio_version}".format(zio_version = zio_version),
            "dev.zio:zio-interop-cats_2.12:2.0.0.0-RC14",
            "dev.zio:zio-logging_2.12:0.2.9",
            "org.tpolecat:doobie-core_2.12:0.8.8",
            "org.tpolecat:doobie-postgres_2.12:0.8.8",
            "org.tpolecat:doobie-hikari_2.12:0.8.8",
            "org.http4s:http4s-blaze-server_2.12:0.21.1",
            "org.http4s:http4s-circe_2.12:0.21.1",
            "org.http4s:http4s-dsl_2.12:0.21.1",
            "com.github.pureconfig:pureconfig_2.12:0.12.3",
            "com.github.ghostdogpr:caliban_2.12:{caliban_version}".format(caliban_version = caliban_version),
            "com.github.ghostdogpr:caliban-http4s_2.12:{caliban_version}".format(caliban_version = caliban_version),
            "io.circe:circe-core_2.12:{circe_version}".format(circe_version = circe_version),
            "io.circe:circe-generic_2.12:{circe_version}".format(circe_version = circe_version),
            "io.circe:circe-parser_2.12:{circe_version}".format(circe_version = circe_version),
            #            "io.tryp:splain_2.12.10:0.5.1",
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
