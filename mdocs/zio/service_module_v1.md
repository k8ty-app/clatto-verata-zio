---
sidebar_position: 3
---

This example is written using zio @ZIO_VERSION@

When building applications with ZIO, often times you will compose similar effects into one service.  ZIO has a service module 
pattern that is suggested - and if you look at the source code for ZIO - you will find this everywhere.  The pattern looks like this:

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
With Scala 2.13 - you would also have a package object, in which you would put the above type (this is not easy to demonstrate with mdoc...).  Any related
model objects - including custom Exceptions would live there as well.