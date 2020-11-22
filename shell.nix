{ pkgs ? import <nixpkgs> { } }:
let
  jre = pkgs.jre8;
  bazel = pkgs.bazel;
in
  pkgs.mkShell {
    buildInputs = [
      bazel
      jre
    ];
  }
