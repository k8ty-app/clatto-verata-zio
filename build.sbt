
val zioVersion = "1.0.7"
val preludeVersion = "1.0.0-RC4"
val zioJsonVersion = "0.1.4"

lazy val cvz = project
  .in(file("."))
  .settings(
    name := "clatto-verata-zio",
    version := "0.0.0",
    scalaVersion := "2.13.5",
    mdocIn := file("mdocs"),
    mdocOut := file("docs"),
    mdocVariables := Map(
      "ZIO_VERSION" -> zioVersion,
      "ZIO_TEST_VERSION" -> zioVersion,
      "PRELUDE_VERSION" -> preludeVersion,
      "ZIO_JSON_VERSION" -> zioJsonVersion
    ),
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zioVersion,
      "dev.zio" %% "zio-test" % zioVersion,
      "dev.zio" %% "zio-prelude" % preludeVersion,
      "dev.zio" %% "zio-json" % zioJsonVersion
    )
  )
  .enablePlugins(MdocPlugin)
