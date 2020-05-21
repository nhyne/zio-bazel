
pin:
	bazel run @unpinned_scala_2_12//:pin

explain:
	bazel build --explain=explain.txt --verbose_explanations tictactoe

shell:
	nix-shell --pure
