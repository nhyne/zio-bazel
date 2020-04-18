load(
    "@rules_scala_annex//rules:scala.bzl",
    "scala_binary",
    "scala_library",
)

java_runtime(
    name = "jdk",
    srcs = select({
        "@bazel_tools//src/conditions:linux_x86_64": ["@jdk8-linux//:jdk"],
        "@bazel_tools//src/conditions:darwin_x86_64": ["@jdk8-osx//:jdk"],
        "@bazel_tools//src/conditions:darwin": ["@jdk8-osx//:jdk"],
    }),
    java = select({
        "@bazel_tools//src/conditions:linux_x86_64": "@jdk8-linux//:java",
        "@bazel_tools//src/conditions:darwin_x86_64": "@jdk8-osx//:java",
        "@bazel_tools//src/conditions:darwin": "@jdk8-osx//:java",
    }),
    visibility = ["//visibility:public"],
)

# Intellij workaround - See https://github.com/higherkindness/rules_scala/issues/246
scala_binary(
    name = "intellij_workaround_2_11",
    scala = "//toolchains:zinc_2_11_12",
    runtime_deps = [
        "@scala_2_11//:org_scala_lang_scala_library",
    ],
)

scala_binary(
    name = "intellij_workaround_2_12",
    scala = "//toolchains:zinc_2_12_10",
    runtime_deps = [
        "@scala_2_12//:org_scala_lang_scala_library",
    ],
)
