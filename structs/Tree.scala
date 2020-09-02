package dev.nhyne.structs

sealed trait Tree[+A]

case object TreeNil extends Tree[Nothing]

case class TreeLeaf[+A](values: A) extends Tree[A]

case class TreeBranch[+A](value: A, left: Tree[A], right: Tree[A]) extends Tree[A]

