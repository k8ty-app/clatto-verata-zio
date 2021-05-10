---
sidebar_position: 3
---

These were written using ZIO JSON @ZIO_JSON_VERSION@ and ZIO Prelude @PRELUDE_VERSION@

zio-json integrates fairly well with zio-prelude as well.  In particularly useful you may find that adding the use of newtypes to your
model classes could help with ensuring proper types and field validation.

To learn more about newtypes - hop on over to the our nifty section on [Prelude Newtypes](/docs/zio-prelude/newtypes)!

Remember the `Car` model from the basic example?

```scala
case class Car(vin: String, model: String, make: String, year: Int)
```

Maybe it'd be nice to take advantage of newtypes and ensure that the year for the car falls between 1981 and 2022.

We can do that!

```scala mdoc
import zio.json._
import zio.prelude._

object Year extends SubtypeSmart[Int](isGreaterThanEqualTo(1981) && isLessThanEqualTo(2022)) {
  val encoder: JsonEncoder[Year] = JsonEncoder.int.contramap(unwrap)
  val decoder: JsonDecoder[Year] = JsonDecoder.int.mapOrFail(decoderWithErrorHandling)
  implicit val codec: JsonCodec[Year] = JsonCodec(encoder, decoder)
  
  private def decoderWithErrorHandling(int: Int) = Year.make(int).toEither.bimap(_.mkString, y => y)
}
type Year = Year.Type

case class Car(vin: String, model: String, make: String, year: Year)
object Car {
  implicit val codec = DeriveJsonCodec.gen[Car]
}

val tooOldJson = """{"vin": "VNW123498", "model": "Bus", "make": "Volkswagen", "year": 1969}"""
val maybeVwBus = tooOldJson.fromJson[Car]

val inRangeJson = """{"vin": "WBAWL73589P473132", "model": "Q50", "make": "Infiniti", "year": 2017}"""
val maybeQ50 = inRangeJson.fromJson[Car]

```
Smart Newtypes help us ensure that the JSON we're receiving is automatically run through some basic data validation.  You could imagine
how this could be useful when used with a REST/gRPC API when receiving payloads for processing!  By using a 'smart' subtype, we provide
some assertions to help validate the value that is to be used with the newtype.  If its not valid, we get a `Validation.Failure`.  We can then turn 
this into an `JsonError` via the `mapOrFail` method.

If you are using newtypes but aren't using smart newtypes - that's ok!  In fact it makes life a bit easier as you can simply extend the `JsonCodec` for the underlying
type via something like this: `JsonCodec.string.xmap(wrap, unwrap)`.

Ultimately, the zio-json project will auto-derive newtypes making this just as simple as any of the other autoderived types.  But until 
then we can at least handle it this way.

Another thing to point out - please note that we built our new `JsonEncoder` and `JsonDecoder` by extending the existing one provided for int.
Since the newtype for Year is a subtype of `Int` - we can leverage this!  You can of course do this without the use of newtypes to build your own
custom `JsonEncoder` or `JsonDecoder`.

