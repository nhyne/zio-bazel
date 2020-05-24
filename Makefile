
pin:
	bazel run @unpinned_scala_2_12//:pin
	bazel run @unpinned_scala_2_13//:pin

explain:
	bazel build --explain=explain.txt --verbose_explanations tictactoe
