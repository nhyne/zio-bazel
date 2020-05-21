{ pkgs ?
  import (fetchTarball {
    url = "https://github.com/NixOS/nixpkgs/archive/28357868f82385003ff7a1fe844779e627ab891b.tar.gz";
    sha256 = "03kz9iygrcdqi0nhjqhzn8z9qd58zhsrkrjxgmswgpdxmfjk6hqb";
  }) {}
}:

with pkgs;

stdenv.mkDerivation rec {
  name = "zio-bazel";
  buildInputs = with rustPackages;
    [
        scala
        bazel
        postgresql
        which
        git
    ];
}
