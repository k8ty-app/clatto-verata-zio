---
sidebar_position: 1
---

These were written using ZIO JSON @ZIO_JSON_VERSION@

ZIO JSON is a fast, secure JSON library for Scala with a tight integration
with ZIO.  We suggest reading about more of the design goals for the project at the
Github  project [README](https://github.com/zio/zio-json/blob/develop/README.md) - particularly regarding [security](https://github.com/zio/zio-json/blob/develop/README.md#security)
as this is a great reason to adopt
ZIO JSON as your Scala JSON library of choice alone!

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

What's nice about zio-json is - that in most cases, you can simply derive the json codec using `DeriveJsonCode.gen[A]` and it will 'do the right thing'.  Let's look at this a bit further...

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

Notice how the DeriveJsonCodec discovered JsonCodecs for `Car` and `java.time.Instant` w/o having to explicitly import additional things or create additional Codecs!