---
sidebar_position: 1
---

The core of everything is the `ZIO[R,E,A]` monad.  This is the core monad behind almost everything
in the zio toolkit.  This is used to compose multiple effects into a program.  

Let's look more closely at `ZIO[R,E,A]`.  Notice it requires multiple types.  These are referred to 
as 'channels' for the monad.  

* `R` is a 'requirements' channel - this is a type that the effect requires
* `E` is the 'error' channel - this is a type that the effect may produce as an error
* `A` is the 'answer' channel - this is the type that the effect will produce upon success

So a `ZIO[Clock, NoTimeError, Instant]` would be an effect that requires a `Clock`, may produce a `NoTimeError` upon error
and should product an `Instant` upon success.

But what if you have an effect that doesn't have a type for the requirements channel, or doesn't produce an error?

That's covered too.  In fact there are a few type aliases for the various permutations.

Its considered best practice to use the type alias that most closely fits what you need, vs simply using `ZIO` everywhere.

Here's a quick cheatsheet:

|What You Want|If You Have|
|---|----|
|`IO[+E, +A]`|`ZIO[Any, E, A]`|
|`Task[+A]`|`ZIO[Any, Throwable, A]`|
|`RIO[-R, +A]`|`ZIO[R, Throwable, A]`|
|`UIO[+A]`|`ZIO[Any, Nothing, A]`|
|`URIO[-R, +A]`|`ZIO[R, Nothing, A]`|
