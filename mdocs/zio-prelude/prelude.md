---
sidebar_position: 1
---

This is just a quick example showcasing the use of mdoc for zio!
This example is written using zio @ZIO_VERSION@ and zio-prelude @PRELUDE_VERSION@

```scala mdoc
import zio._
import zio.console._
import zio.prelude.AnySyntax

val runtime = Runtime.default

val speak: Seq[String] => UIO[Seq[String]] = str => ZIO.succeed(str)
val slowly: UIO[Seq[String]] => UIO[String] = z => z.map(_.map(_ + "... ").mkString)
val printResult: UIO[String] => ZIO[Console, Option[Nothing], Unit] = 
z => z.flatMap(str => putStrLn(str))

val words = Seq("Clatto", "Verata", "zoodle", "It's defineitly a Z-word!!!")
val speakWords = for {
_ <- speak(words) |> slowly |> printResult
} yield()

runtime.unsafeRun(speakWords)
```



