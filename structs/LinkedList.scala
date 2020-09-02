package dev.nhyne.structs

sealed trait LinkedList[+A]

case object Nil extends LinkedList[Nothing]

case class Cons[+A](head: A, tail: LinkedList[A]) extends LinkedList[A]

object LinkedList {
    def apply[A](as: A*): LinkedList[A] =
        if (as.isEmpty) Nil
        else Cons(as.head, apply(as.tail: _*))
}


