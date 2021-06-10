
val zioVersion = "1.0.9"
val preludeVersion = "1.0.0-RC5"
val zioJsonVersion = "0.1.5"
val zioOpticsVersion = "0.1.0"

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
      "ZIO_JSON_VERSION" -> zioJsonVersion,
      "ZIO_OPTICS_VERSION" -> zioOpticsVersion,
      "ZIO_QUILL_VERSION" -> "3.7.1"
    ),
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zioVersion,
      "dev.zio" %% "zio-test" % zioVersion,
      "dev.zio" %% "zio-prelude" % preludeVersion,
      "dev.zio" %% "zio-json" % zioJsonVersion,
      "dev.zio" %% "zio-optics" % zioOpticsVersion,
      "dev.zio" %% "zio-macros" % zioVersion
    ),
    scalacOptions += "-Ymacro-annotations"
  )
  .enablePlugins(MdocPlugin)
