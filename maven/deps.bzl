load("@rules_jvm_external//:defs.bzl", "maven_install")
load("@rules_jvm_external//:specs.bzl", "maven")
load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_file")

def install_maven_deps():
    maven_install(
        name = "scala_2_12",
        artifacts = [
            "dev.zio:zio-test_2.12:1.0.0-RC18-2",
            "dev.zio:zio-test-sbt_2.12:1.0.0-RC18-2",
            "com.beachape:enumeratum_2.12:1.5.13",
            #            "dev.zio:zio-config_2.12:1.0.0-RC16-1",
            #            "dev.zio:zio-config-magnolia_2.12:1.0.0-RC16-1",
            #            "dev.zio:zio-config-magnolia_2.12:1.0.0-RC16-1",
            #            "dev.zio:zio-config-typesafe_2.12:1.0.0-RC16-1",
            #            "dev.zio:zio-logging_2.12:0.2.6",
            #            "dev.zio:zio-logging-slf4j_2.12:0.2.6",
            #            "dev.zio:zio-metrics-prometheus_2.12:0.2.2",
            #            "dev.zio:zio-macros-core_2.12:0.5.0",
            #            "dev.zio:zio-macros-test_2.12:0.5.0",
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
