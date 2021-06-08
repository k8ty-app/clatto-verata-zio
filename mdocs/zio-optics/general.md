---
sidebar_position: 1
title: General Usage
---

`zio-optics` is a brand new project in `development` state that provides an optics library for the ZIO ecosystem.  This 
project is very new and very raw - examples here may go out of date quickly as the project evolves.  We'll do our best to 
keep them up to date.

Given the current state of things, here's a basic example using 'Lens' and 'Optics':

### Getting Started
To get started with `zio-optics` let's dust off our `Item` model class.  We're going to create a `Lens` for it so 
we can easily update values on a group of items w/o a bunch of boilerplate.

In its current state `zio-optics` does not auto-derive any code for user defined data types.  This will change shortly, so for the sake of the example, let's
define our own `Lens` - its pretty simple.  For each value you want a `Lens` for you need to define a 'setter' and 'getter' function for each:

```scala mdoc 
import zio._
import zio.console._
import zio.optics._

import java.time.Instant

val runtime = Runtime.default

case class Item(id: Long, name: String, description: String, price: Double, createdAt: Instant)

object Item {

  def name: Lens[Item, String] = Lens(
    item => Right(item.name),
    name => item => Right(item.copy(name = name))
  )

  def price: Lens[Item, Double] = Lens(
    item => Right(item.price),
    price => item => Right(item.copy(price = price))
  )

  def calcDiscount(price: Double): Double = price - (price * .1)
}
```

You define these on the companion class for your user defined type.  Note we also have a pure function here as well that adjusts a given price by 10% - we'll use that in a bit...

### Using the Optics

So, let's say we have a set of items in a store that we want to mark down the prices by 10%.  Now that we have a `Lens` defined, we can do that pretty easily.

So, let's setup a few things.  

First let's set up some data to work for in a `Chunk[Item]` and define a small function that checks a price against a boundary.

Then we'll setup an `Optic` that is a `Traversal[Chunk[Item], Double]`.  That this does is traverses a `Chunk[Item]`, applies a filter to it and then applies a `Lens` to each element in the `Traversal`.
This is a bit of a 'hack' until the library improves - since `Optic` can't work off a `Chunk[A]` w/o a filter at this point in time.

Now that we have all the pieces in place, let's use the `Optic` we have defined:

```scala mdoc
val items: Chunk[Item] = Chunk(
  Item(1, "Boomstick", "This..is my boomstick!", 255.50, Instant.now),
  Item(2, "Helm", "A rather cheap looking helm", 19.99, Instant.now)
)

def hasPrice(item: Item) = item.price > 0.00

def discountItems: Traversal[Chunk[Item], Double] =
  Optic.filter(hasPrice).foreach(Item.price)
  val program: ZIO[Console, Throwable, Unit] =
  for {
    _       <- putStrLn(s"All items: \n\t ${items.map(i => (i.name, i.price)).mkString(", \n\t ")}")
    _       <- putStrLn("Applying 10% discount to all items!")
    updated <- ZIO.fromEither(discountItems.update(items)(_.map(Item.calcDiscount)))
    _       <- putStrLn(s"All items discounted: \n\t ${updated.map(i => (i.name, i.price)) mkString (", \n\t ")}")
  } yield ()



runtime.unsafeRun(program)

```