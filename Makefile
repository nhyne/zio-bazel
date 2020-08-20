
pin_12:
	bazel run @unpinned_scala_2_12//:pin

pin_13:
	bazel run @unpinned_scala_2_13//:pin

pin: pin_12 pin_13

explain:
	bazel build --explain=explain.txt --verbose_explanations tictactoe
