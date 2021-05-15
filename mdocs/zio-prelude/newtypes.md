---
sidebar_position: 1
title: Newtypes
---

Some quick examples of ZIO Prelude Newtypes.

## Newtype

From the scaladoc:

"Newtypes wrap an existing type and have the same representation as the underlying type at runtime but are treated as
distinct types by the Scala compiler. Newtypes can be used to increase type safety in modeling a domain, for example by
creating separate types for Meter and Foot. They can also be used to provide coherent instances for types that can
support more than one version of a typeclass. For example, the And and Or types can be used to distinguish boolean
conjunction and disjunction. To create a newtype, simply implement an object which extends Newtype[A], where A is the
underlying type you are wrapping. Then implement a type alias for Type in the object you create."

```scala mdoc
import zio.prelude._

object Meter extends Newtype[Double]
type Meter = Meter.Type

implicit class MeterSyntax(private val self: Meter) {
   def +(that: Meter): Meter =
     Meter.wrap(Meter.unwrap(self) + Meter.unwrap(that))
 }
 
 val distance1 = Meter(3.4)
 val distance2 = Meter(10.5)
 
 val totalDistance = distance1 + distance2
```

## Subtype

Newtypes don't inherit operations from the type you are extending - if you'd like to extend the operations from the
parent type, use a `Subtype` instead.

```scala mdoc
import java.util.UUID
import zio.prelude._

object UserId extends Subtype[String]
type UserId = UserId.Type

val ashsId = UserId(UUID.randomUUID().toString)
val size = ashsId.length

```

## SubtypeSmart

Another useful thing are 'smart' newtypes and subtypes. Those accept an Assertion via zio-test to help validate the
values they wrap. When you create a smart newtype or subtype, you must create them using the `make` method - you can't
use the default `apply` methods outside of the companion class. The `make` method will run the assertion and return your
results wrapped in a `ZValidation`

```scala mdoc

import zio.test.Assertion
import zio.test.Assertion._

object SmartUserId extends SubtypeSmart[String](hasSizeString(Assertion.equalTo(36)))
type SmartUserId = SmartUserId.Type

val badId = SmartUserId.make("bad_id")
val goodId = SmartUserId.make(UUID.randomUUID.toString)
```

Newtypes are magical and extremely useful!
