---
sidebar_position: 2
---

These were written using ZIO JSON @ZIO_JSON_VERSION@

The basic overview showed some straightforward examples that work really well out of the box if your JSON conforms with Scala camelcase naming conventions
and if your json field names match your case class names exactly. `@jsonField()` annotation to the rescue!

```scala mdoc
import zio.json._

case class Car(vin: String, model: String, make: String, year: Int, @jsonField("model_trim") trim: String)
object Car {
  implicit val codec = DeriveJsonCodec.gen[Car]
}

val carSnakeCaseJson = """{"vin": "WBAWL73589P473132", "model": "Q50", "make": "Infiniti", "year": 2021, "model_trim": "400 Red Sport"}"""

val carFromJson = carSnakeCaseJson.fromJson[Car]


```

But what if we wanted to have a little class hierarchy with our model?  Sure, no problem...

```scala mdoc
import zio.json.{DeriveJsonCodec, DeriveJsonDecoder, DeriveJsonEncoder}
sealed trait Vehicle {
  def vin: String
}
case class Truck(override val vin: String, model: String, make: String, year: Int, hasSleeper: Boolean) extends Vehicle
@jsonHint("car") case class PassengerCar(override val vin: String, model: String, make: String, year: Int, seats: Int) extends Vehicle

object Vehicle {
  implicit val codec = DeriveJsonCodec.gen[Vehicle]
}

val truckJson = """{"Truck": {"vin": "1NKDX4TX7PR47330", "model": "T800", "make": "Kenworth", "year": 2015, "hasSleeper": false}}"""
val truck = truckJson.fromJson[Vehicle]

val passengerCarJson = """{"car": {"vin": "WBAWL73589P473132", "model": "Q50", "make": "Infiniti", "year": 2021, "seats": 5}}"""
val passengerCar = passengerCarJson.fromJson[Vehicle]

val qx55: Vehicle = PassengerCar(vin = "WBAWL73589P473132", model = "QX55", make = "Infiniti", year = 2022, seats = 5)
val qx55Json = qx55.toJson



```