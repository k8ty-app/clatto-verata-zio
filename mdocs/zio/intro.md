---
sidebar_position: 1
---

What is ZIO?  At its core, ZIO is a Scala version of the Haskell `IO` monad. In functional programming, a function is either pure (no side effects) or impure (produces side effects).
Typically you want more pure functions than impure, but in some cases you just can't avoid it.  For example, connecting to a database, a webservice, etc are all examples of impure functions. 
IO Monads are used in functional programming (FP) to wrap impure functions.

However, ZIO is more than just an IO monad - its a complete toolkit for building resilient, asynchronous applications 
using a functional programming approach.  In fact, while there are other Scala functional programming libraries out there
ZIOs approach to pragmatism and ease of use makes it extremely effective.

Here is a simple ZIO application written using ZIO @ZIO_VERSION@

```scala mdoc
import zio._
import zio.console._

val runtime = Runtime.default

val speakWords = for {
_ <- putStrLn("Clatto..")
_ <- putStrLn("Verata...")
_ <- putStrLn("Necktie?")
} yield()

runtime.unsafeRun(speakWords)
```
When building applications in ZIO, you are composing a series of effects.  These effects
are wrapped in a ZIO and composed together.  You can then take all these composed effects
and execute them with a given runtime.  It's at that point that the code contained within the effects
executed.
