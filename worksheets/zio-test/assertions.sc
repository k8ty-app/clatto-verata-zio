import $ivy.{
  `dev.zio::zio:1.0.7`,
  `dev.zio::zio-test:1.0.7`
}

import zio._
import zio.test._
import zio.test.Assertion._

import scala.util.Try

// Asserting "vals"
val groovy: String = "Groovy"
Seq(
  assert(groovy)(equalTo("Groovy")),
  assert(groovy)(isNonEmptyString),
  assert(groovy)(startsWithString("G")),
  assert(groovy)(endsWithString("y")),
  assert(groovy)(containsString("oo")), // notice the somethingString
  assert(groovy)(hasSizeString(equalTo(6))), // Notice we're asserting assertions
)

//might be tempted to
groovy.startsWith("") || groovy.contains("")
// asserting options
val defined: Option[String] = Some(groovy)
val undefined: Option[String] = None

Seq(
  assert(defined)(isSome),
  assert(undefined)(isNone),
  assert(defined)(isSome(equalTo(groovy))),
).foreach(t => println(t.isSuccess))

// asserting Trys
val success: Try[Int] = Try(42)
val failure: Try[Int] = Try(1 / 0)

Seq(
  assert(success)(isSuccess),
  assert(success)(isSuccess(equalTo(42))),
  assert(failure)(isFailure),
  assert(failure)(isFailure(isSubtype[ArithmeticException](anything)))
).foreach(t => println(t.isSuccess))

// assertM
val runtime = Runtime.default
def divide(a: Int, b: Int): Task[Int] = Task(a/b)

Seq(
  assertM(divide(4, 2))(equalTo(3)),
  assertM(ZIO.succeed(groovy))(equalTo(groovy))
).map(a => a.run).map(a => runtime.unsafeRun(a).map(_.isSuccess)).foreach(println)
