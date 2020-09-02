package dev.nhyne.tree

object Main {
    def main(args: Array[String]): Unit = {
      println("cool")
    }
}

case class Tree[A](root: Option[Node[A]])
case class Node[A](value: A, left: Option[Node[A]], right: Option[Node[A]])


object Tree {
    def insert[A](tree: Tree[A], node: Node[A]): Tree[A] = {
        ???
    }
}
