---
sidebar_position: 1
title: Intro to ZIO
---

What is ZIO? At its core, ZIO is a Scala version of the Haskell `IO` monad. In functional programming, a function is
either pure (no side effects) or impure (produces side effects). Typically, you want more pure functions than impure, but
in some cases you just can't avoid it. For example, connecting to a database, a webservice, etc are all examples of
impure functions. IO Monads are used in functional programming (FP) to wrap impure functions.

However, ZIO is more than just an IO monad - it's a complete toolkit for building resilient, asynchronous applications
using a functional programming approach. In fact, while there are other Scala functional programming libraries out there
ZIOs approach to pragmatism and ease of use makes it extremely effective.

## A Brief Example

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

## A Closer Look

The core of everything is the `ZIO[R,E,A]` monad. This is the core monad behind almost everything in the zio toolkit.
This is used to compose multiple effects into a program.

Let's look more closely at `ZIO[R,E,A]`. Notice it requires multiple types. These are referred to as 'channels' for the
monad.

* `R` is a 'requirements' channel - this is a type that the effect requires
* `E` is the 'error' channel - this is a type that the effect may produce as an error
* `A` is the 'answer' channel - this is the type that the effect will produce upon success

So a `ZIO[Clock, NoTimeError, Instant]` would be an effect that requires a `Clock`, may produce a `NoTimeError` upon
error and should product an `Instant` upon success.

But what if you have an effect that doesn't have a type for the requirements channel, or doesn't produce an error?

That's covered too. In fact there are a few type aliases for the various permutations.

Its considered best practice to use the type alias that most closely fits what you need, vs simply using `ZIO`
everywhere.

## Type Alias Cheatsheet

Here's a quick cheatsheet:

|What You Want|If You Have|
|---|----|
|`IO[+E, +A]`|`ZIO[Any, E, A]`|
|`Task[+A]`|`ZIO[Any, Throwable, A]`|
|`RIO[-R, +A]`|`ZIO[R, Throwable, A]`|
|`UIO[+A]`|`ZIO[Any, Nothing, A]`|
|`URIO[-R, +A]`|`ZIO[R, Nothing, A]`|
