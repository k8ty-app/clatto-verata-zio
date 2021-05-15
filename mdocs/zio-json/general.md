---
sidebar_position: 1
title: General Usage
---

ZIO JSON is a fast, secure JSON library for Scala with a tight integration with ZIO. We suggest reading about more of
the design goals for the project at the Github project [README](https://github.com/zio/zio-json/blob/develop/README.md)
- particularly regarding [security](https://github.com/zio/zio-json/blob/develop/README.md#security)
as this is a great reason to adopt ZIO JSON as your Scala JSON library of choice alone!

## Basic Usage

We'll start off by showing some basic/simple ways to use zio-json here:

```scala mdoc
import zio.json._

case class Car(vin: String, model: String, make: String, year: Int)

object Car {
  implicit val codec = DeriveJsonCodec.gen[Car]
}

val carJson = """{"vin": "WBAWL73589P473132", "model": "Q50", "make": "Infiniti", "year": 2021}"""

var carFromJson = carJson.fromJson[Car]

val badJson = """{"vin": "asdfasdf", "make": 1234 }"""
val carFromBadJson = badJson.fromJson[Car]

val qx55 = Car(vin = "WBAWL73589P473132", model = "QX55", make = "Infiniti", year = 2022)
val qx55ToJson = qx55.toJson
```

What's nice about zio-json is - that in most cases, you can simply derive the json codec using `DeriveJsonCode.gen[A]`
and it will 'do the right thing'. Let's look at this a bit further...

```scala mdoc
import java.time._
import zio.json._

case class Invoice(car: Car, price: Double, saleDate: Instant)

object Invoice {
  implicit val codec = DeriveJsonCodec.gen[Invoice]
}

val salesDate = Instant.now.toString

val invoiceJson =
  s"""
    |{
    |  "price": "55000.00",
    |  "saleDate" : "$salesDate",
    |  "car": {
    |   "vin": "WBAWL73589P473132", 
    |   "model": "Q50", 
    |   "make": "Infiniti", 
    |   "year": 2021
    |   }
    |}
    |""".stripMargin

val invoiceFromJson = invoiceJson.fromJson[Invoice]

val qx55Invoice = Invoice(car = qx55, price = 65000.00, saleDate = Instant.now)
val qx55InvoiceJson = qx55Invoice.toJson


```

Notice how the DeriveJsonCodec discovered JsonCodecs for `Car` and `java.time.Instant` w/o having to explicitly import
additional things or create additional Codecs!

## Advanced Usage


The basic overview showed some straightforward examples that work really well out-of-the-box if your JSON conforms with
Scala camelcase naming conventions and if your json field names match your case class names exactly. `@jsonField()`
annotation to the rescue!

```scala mdoc:reset
import zio.json._

case class Car(vin: String, model: String, make: String, year: Int, @jsonField("model_trim") trim: String)
object Car {
  implicit val codec = DeriveJsonCodec.gen[Car]
}

val carSnakeCaseJson = """{"vin": "WBAWL73589P473132", "model": "Q50", "make": "Infiniti", "year": 2021, "model_trim": "400 Red Sport"}"""

val carFromJson = carSnakeCaseJson.fromJson[Car]


```

But what if we wanted to have a little class hierarchy with our model? Sure, no problem...

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
