---
sidebar_position: 3
title: Service Module Pattern
---

## Version 1

When building applications with ZIO, often times you will compose similar effects into one service. ZIO has a service
module pattern that is suggested - and if you look at the source code for ZIO - you will find this everywhere. The
pattern looks like this:

```scala mdoc

import java.time._
import zio._

object ExampleClock {
  // alternatively - this can live in a package object
  type ExampleClock = Has[ExampleClock.Service]
  trait Service {
    def getCurrentTime: Task[Instant]
  }
  // accessor method; you can generate these with @accessible annotated on the object
  def getCurrentTime: RIO[ExampleClock, Instant] = RIO.accessM(_.get.getCurrentTime)

  // our 'live' instance of the service
  def live: ULayer[ExampleClock] = ZLayer.succeed {
    new Service {
      override def getCurrentTime = Task(Instant.now)
    }
  }
}

```

With Scala 2.13 - you would also have a package object, in which you would put the above type (this is not easy to
demonstrate with mdoc...). Any related model objects - including custom Exceptions would live there as well.

## Version 2

ZIO 2.0 has a lot of work put into adjusting how ZLayers are composed - that will hopefully simplify the process for
people new to ZIO. To prepare for the 2.0 release, an alternative take on the service module pattern has been
introduced. Following this will help ensure that code you're writing against version 1.0.x will port over to 2.0 easily.
The main change is that a lot of the helper methods on `ZLayer` - those that start with `from` - i.e. `fromService`
and `fromEffect` are going to be removed in the name of simplicity.

So what does this new pattern look like?

```scala mdoc
import java.time.Instant

import zio._
import zio.clock.Clock

case class Car(vin: String, model: String, make: String, year: String)

case class Valuation(price: Double, expiration: Instant)

trait CarValuationService {
  def getValuation(car: Car): Task[Valuation]
}

object CarValuationService {
  
  // accessor
  def getValuation(car: Car): RIO[Has[CarValuationService], Valuation] = RIO.accessM(_.get.getValuation(car))
  
  val live: URLayer[Clock, Has[CarValuationService]] = {
    for {
      clock <- ZIO.environment[Clock]
    } yield new CarValuationService {
      override def getValuation(car: Car): Task[Valuation] = ???
    }
  }.toLayer
}
```

To highlight the differences:

* the service trait is no longer called `Service` and is defined outside the object
* the layers are build by invoking `toLayer` on a ZIO vs using `ZLayer` itself
* along with the above - any dependencies that you have can be accessed from the ZIO `environment` or as a `service`
  directly and composed with each other
* there is no longer a type alias for the service dependency; use `Has[MyService]` directly instead
