package models.types


sealed trait Bracket[+A] {
  def getValue: A = this match {
    case NodeMatch(value, left, right) => value
    case LeafMatch(value) => value

  }


  def map[B](f: (A) => B): Bracket[B] = this match {
    case NodeMatch(value, left, right) => NodeMatch(f(value), left.map(f), right.map(f))
    case LeafMatch(value) => LeafMatch(f(value))
  }

  def nodeMap[B](f: (Bracket[A]) => B): Bracket[B] = this match {
    case n: NodeMatch[A] => NodeMatch[B](f(n), n.left.nodeMap(f), n.right.nodeMap(f))
    case l: LeafMatch[A] => LeafMatch(f(l))
  }

  def fold[B](f: (A) => B)(g: (B, B) => B): B = this match {
    case NodeMatch(value, left, right) => g(f(value), g(left.fold(f)(g), right.fold(f)(g)))
    case LeafMatch(value) => f(value)
  }

  def realFold[B](f: (A) => B)(g: (A, B, B) => B): B = this match {
    case NodeMatch(value, left, right) => g(value, left.realFold(f)(g), right.realFold(f)(g))
    case LeafMatch(value) => f(value)
  }

  def size = {
    realFold(_ => 1)((a, b, c) => 1 + b)
  }

  def getRoundList(roundNr: Int): List[A] = this match {
    case n: NodeMatch[A] =>
      if (roundNr > 0) {
        n.left.getRoundList(roundNr - 1) ::: n.right.getRoundList(roundNr - 1)
      } else if (roundNr == 0) {
        n.value :: Nil
      } else {
        Nil
      }
    case l: LeafMatch[A] =>
      if (roundNr == 0) {
        l.value :: Nil
      } else {
        Nil
      }
  }
}


case class NodeMatch[A](value: A, left: Bracket[A], right: Bracket[A]) extends Bracket[A]

case class LeafMatch[A](value: A) extends Bracket[A]

