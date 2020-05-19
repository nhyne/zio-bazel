load("@rules_jvm_external//:defs.bzl", "maven_install")
load("@rules_jvm_external//:specs.bzl", "maven")
load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_file")

zio_version = "1.0.0-RC18-2"

def install_maven_deps():
    maven_install(
        name = "scala_2_12",
        artifacts = [
            "dev.zio:zio-test_2.12:{zio_version}".format(zio_version = zio_version),
            "dev.zio:zio-test-sbt_2.12:{zio_version}".format(zio_version = zio_version),
            "dev.zio:zio-query_2.12:0.2.0",
            "dev.zio:zio-interop-cats_2.12:2.0.0.0-RC14",
            "com.beachape:enumeratum_2.12:1.5.13",
            "org.tpolecat:atto-core_2.12:0.6.5",
            "org.tpolecat:atto-refined_2.12:0.6.5",
            "org.tpolecat:doobie-core_2.12:0.8.8",
            "org.tpolecat:doobie-postgres_2.12:0.8.8",
            "org.http4s:http4s-blaze-server_2.12:0.21.1",
            "org.http4s:http4s-circe_2.12:0.21.1",
            "org.http4s:http4s-dsl_2.12:0.21.1",
            #            "io.tryp:splain_2.12.10:0.5.1",
        ],
        repositories = [
            "https://maven.google.com",
            "https://repo1.maven.org/maven2",
        ],
        fetch_sources = True,
        version_conflict_policy = "pinned",
        use_unsafe_shared_cache = True,
        maven_install_json = "//maven:scala_2_12_install.json",
    )
