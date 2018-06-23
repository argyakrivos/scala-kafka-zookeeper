import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
  lazy val kafka = "org.apache.kafka" %% "kafka" % "1.1.0"
  lazy val curator = "org.apache.curator" % "curator-test" % "4.0.1"
  lazy val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"
}
