---
sidebar_position: 1
---

## A quick look at `String`

```scala mdoc
import zio._
import zio.test._
import zio.test.Assertion._

val groovy: String = "Groovy"

// TestResult isn't much to look at, so we well check `.isSuccess`
assert(groovy)(equalTo("Groovy"))

assert(groovy)(equalTo("Groovy")).isSuccess
assert(groovy)(isNonEmptyString).isSuccess
assert(groovy)(startsWithString("G")).isSuccess
assert(groovy)(endsWithString("y")).isSuccess
assert(groovy)(containsString("oo")).isSuccess
assert(groovy)(hasSizeString(equalTo(6))).isSuccess
assert(groovy)(hasSizeString(equalTo(7))).isSuccess

```

Notice the `somethingString` assertions - you might be tempted to reach for
`.startsWith` or `.contains` like you would on `String`, but those aren't quite the right type, and you'll end up going
down a rabbit hole. As you are becoming more familiar with the zio libraries, if you ever feel like getting the types to
line up is more complicated than it should be, perhaps you have reached for the wrong method!

Also notice that once we start computing things of a different type (i.e. string length; an Int), we have an Assertion
of an Assertion -- `hasSizeString(equalTo(6))`. We are passing an Assertion of being equalTo the Int value of 6 to the
hasSizeString Assertion. It's Assertions all the way down! Get used to that.

## A quick look at `Option`

```scala mdoc
import zio._
import zio.test._
import zio.test.Assertion._

val defined: Option[String] = Some(groovy)
val undefined: Option[String] = None

assert(defined)(isSome).isSuccess
assert(undefined)(isNone).isSuccess
assert(defined)(isSome(equalTo(groovy))).isSuccess
```

As you can see, if you feel comfortable with "simple things" like Strings, more advanced types work the same - Assertions,
and Assertions of Assertions. Again, note that it is not our familiar Option operations like `.isDefined` or `.contains(groovy)`

## A Quick look at `Try`

```scala mdoc
import zio._
import zio.test._
import zio.test.Assertion._
import scala.util.Try

val success: Try[Int] = Try(42)
val failure: Try[Int] = Try(1 / 0)

assert(success)(isSuccess).isSuccess
assert(success)(isSuccess(equalTo(42))).isSuccess
assert(failure)(isFailure).isSuccess
assert(failure)(isFailure(isSubtype[ArithmeticException](anything))).isSuccess

```

When working with things that can fail, we can match on the type of failure with Assertions like
`isSubtype`. In out case, we checked for a particular exception of type `ArithmeticException`.

## assertM vs assert

The difference between assertM and assert, is that assertM works on _effects_; they
will need to be run in order to compute the result.

```scala mdoc
import zio._
import zio.test._
import zio.test.Assertion._
import scala.util.Try

def divide(a: Int, b: Int): Task[Int] = Task(a/b)

// These are effects, so they need to be run to know their status
val a = assertM(divide(4, 2))(equalTo(2)).isSuccess
val b = assertM(divide(4, 0))(equalTo(2)).isFailure
val c = assertM(ZIO.succeed(groovy))(equalTo(groovy)).isSuccess

val runtime = Runtime.default

// We can see the results, once ran
runtime.unsafeRun(a)
runtime.unsafeRun(b)
runtime.unsafeRun(c)

```

There is a similar pattern to `test` and `testM`. In test suites, I think it's pretty typical to see a `testM`
running several effects, which produce a result, and then `assert`ing on that result.
