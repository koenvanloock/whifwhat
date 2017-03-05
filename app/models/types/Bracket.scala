package models.types


sealed trait Bracket[+A] {
  def getValue: A = this match {
    case BracketNode(value, left, right) => value
    case BracketLeaf(value) => value

  }


  def map[B](f: (A) => B): Bracket[B] = this match {
    case BracketNode(value, left, right) => BracketNode(f(value), left.map(f), right.map(f))
    case BracketLeaf(value) => BracketLeaf(f(value))
  }

  def nodeMap[B](f: (Bracket[A]) => B): Bracket[B] = this match {
    case n: BracketNode[A] => BracketNode[B](f(n), n.left.nodeMap(f), n.right.nodeMap(f))
    case l: BracketLeaf[A] => BracketLeaf(f(l))
  }

  def fold[B](f: (A) => B)(g: (B, B) => B): B = this match {
    case BracketNode(value, left, right) => g(f(value), g(left.fold(f)(g), right.fold(f)(g)))
    case BracketLeaf(value) => f(value)
  }

  def realFold[B](f: (A) => B)(g: (A, B, B) => B): B = this match {
    case BracketNode(value, left, right) => g(value, left.realFold(f)(g), right.realFold(f)(g))
    case BracketLeaf(value) => f(value)
  }

  def size = {
    realFold(_ => 1)((a, b, c) => 1 + b)
  }

  def getRoundList(roundNr: Int): List[A] = this match {
    case n: BracketNode[A] =>
      if (roundNr > 0) {
        n.left.getRoundList(roundNr - 1) ::: n.right.getRoundList(roundNr - 1)
      } else if (roundNr == 0) {
        n.value :: Nil
      } else {
        Nil
      }
    case l: BracketLeaf[A] =>
      if (roundNr == 0) {
        l.value :: Nil
      } else {
        Nil
      }
  }

  def toList: List[A] = this match {
    case b:BracketNode[A] => b.value :: b.left.toList ::: b.right.toList
    case l:BracketLeaf[A] => List(l.value)

  }
}


case class BracketNode[A](value: A, left: Bracket[A], right: Bracket[A]) extends Bracket[A]

case class BracketLeaf[A](value: A) extends Bracket[A]

