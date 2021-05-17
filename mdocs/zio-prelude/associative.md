---
sidebar_position: 3
title: Associative
---

Some quick examples using `Associative[A]`

## Associative[A]

`Associative[A]` contains a binary operation you to `combine` types `A`.  

Things that are associative will follow the associativity law.

From the scaladoc: 
```
The associativity law states that for some binary operator *, for all values a1, a2, and a3, the following must hold:

 (a1 * a2) * a3 === a1 * (a2 * a3)
```

If you want to make your types Associative - its fairly easy:

```scala mdoc 
import java.time.Instant
import zio.prelude._
import zio._
import zio.console._

val runtime = Runtime.default

// simple model
case class InvoiceItem(name: String, description: String, unitPrice: Double, quantity: Int) {
  def total: Double = unitPrice * quantity
}

case class Invoice(date: Instant, total: Double, items: Seq[InvoiceItem])

implicit object InvoiceAssociative extends Associative[Invoice] {

    override def combine(l: => Invoice, r: => Invoice): Invoice = {
      val combinedItems: Seq[InvoiceItem] = l.items ++ r.items
      val newTotal: Double                = combinedItems.map(_.total).sum
      val newDate                         = if (l.date.toEpochMilli > r.date.toEpochMilli) l.date else r.date
      Invoice(date = newDate, items = combinedItems, total = newTotal)
    }
}

val program: ZIO[Console, Throwable, Unit] = {
    val items: Seq[InvoiceItem] = Seq(InvoiceItem(name = "Boomstick", description = "This...is my Boomstick!", unitPrice = 255.50, quantity = 1))
    for {
      invoice1   <- Task(Invoice(Instant.now, items.map(_.total).sum, items))
      invoice2   <- Task(Invoice(Instant.now, items.map(_.total).sum, items))
      newInvoice <- Task(invoice1 <> invoice2)
      _          <- putStrLn(s"Combined Invoice: $newInvoice")
    } yield ()
}

runtime.unsafeRun(program)

```

To make a type associative, simply create an implicit object that extends `Associative[A]` for your type and implement the `combine` method.

Please note, `<>` is the combine operator, provided by `zio.prelude.AssociativeSyntax`.

There are many implicit conversions available for a number of built in types and variations.  As long as you define one for your type
it will then enable it to be used with the others seamlessly.
