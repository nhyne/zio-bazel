{ pkgs ?
  import (fetchTarball {
    url = "https://github.com/NixOS/nixpkgs/archive/29d57de30101b51b016310ee51c2c4ec762f88db.tar.gz";
    sha256 = "1wjljkffb3gzdvpfc4v98mrhzack6k9i7860n8cf5nipyab6jbq9";
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
