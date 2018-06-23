import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "me.akrivos",
      scalaVersion := "2.12.6",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "scala-kafka-zookeeper",
    libraryDependencies ++= Seq(
      logback,
      kafka,
      curator % Test,
      scalaTest % Test
    )
  )
