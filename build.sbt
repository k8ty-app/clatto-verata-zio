
val zioVersion = "1.0.7"

lazy val cvz = project
  .in(file("."))
  .settings(
    name := "clatto-verata-zio",
    version := "0.0.0",
    scalaVersion := "2.13.5",
    mdocIn := file("mdocs"),
    mdocOut := file("docs"),
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zioVersion
    )
  )
  .enablePlugins(MdocPlugin)
