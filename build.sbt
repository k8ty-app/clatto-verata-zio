
val zioVersion = "1.0.7"
val preludeVersion = "1.0.0-RC4"

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
      "PRELUDE_VERSION" -> preludeVersion
    ),
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zioVersion,
      "dev.zio" %% "zio-prelude" % preludeVersion
    )
  )
  .enablePlugins(MdocPlugin)
