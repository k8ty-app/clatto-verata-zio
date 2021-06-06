---
sidebar_position: 4
title: Ordering
---

If you want to provide ordering to your data model, `zio-prelude` provides the `Ord` and `Ordering`.

`Ord` sets up the total ordering for your data model.

`Ordering` then helps provide the answer when comparing items.  And `Ordering` will give you one of the following results:

`GreaterThan`, `LessThan`, `Equals`

Here's an example utilizing Scala 2 `Enumeration` to model a game of rock,paper, scissors:

```scala mdoc
import zio._
import zio.console._
import zio.prelude._
import Hand.Hand



  object Hand extends Enumeration {
    type Hand = Value

    val Paper    = Value(1, "Paper")
    val Scissors = Value(2, "Scissors")
    val Rock     = Value(3, "Rock")

  }

  // Setup Ord based off a given Hand and its ordinal value in the enum
  implicit val handOrd: Ord[Hand] = Ord[Int].contramap[Hand](_.id)

  // provide answers to the extends of the model and then it can sort out the rest
  def beats(a: Hand, b: Hand): Ordering = (a, b) match {
    case (Hand.Rock, Hand.Paper) => Ordering.LessThan
    case (Hand.Paper, Hand.Rock) => Ordering.GreaterThan
    case _                       => a =?= b
  }

  def compare(first: Int, second: Int) =
    for {
      firstHand  <- ZIO.effectTotal(Hand.values.toList(first))
      secondHand <- ZIO.effectTotal(Hand.values.toList(second))
      _          <- putStrLn(s"${firstHand} vs ${secondHand}: ${beats(firstHand, secondHand)}")
    } yield ()

  val program =
    for {
      _ <- ZIO.foreach_((0 until 3).toList) { first =>
        ZIO.foreach((0 until 3).toList) { second =>
          compare(first, second)
        }
      }
    } yield ()

val runtime = Runtime.default

runtime.unsafeRun(program)



```

Here we define an `Ord` based on the ordial value of each `Hand` in the enumeraiton.

After we have that we just need to provide answers to a couple solutions and then it can
determine the answers for any other combination.