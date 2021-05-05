---
sidebar_position: 1
---

This is just a quick example showcasing the use of mdoc for zio!
This example is written using zio @ZIO_VERSION@

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

