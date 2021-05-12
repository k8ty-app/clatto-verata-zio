---
sidebar_position: 4
---

This example is written using zio @ZIO_VERSION@

ZIO 2.0 has a lot of work put into adjusting how ZLayers are composed - that will hopefully 
simplify the process for people new to ZIO.  To prepare for the 2.0 release, an alternative take on the 
service module pattern has been introduced.  Following this will help ensure that code you're writing against
version 1.0.x will port over to 2.0 easily. The main change is that a lot of the helper methods on `ZLayer` - those that
start with `from` - i.e. `fromService` and `fromEffect` are going to be removed in the name of simplicity.  

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
* along with the above - any dependencies that you have can be accessed from the ZIO `environment` or as a `service` directly and composed with each other
* there is no longer a type alias for the service dependency; use `Has[MyService]` directly instead