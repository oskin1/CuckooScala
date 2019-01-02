name := "CuckooScala"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "com.google.guava" % "guava" % "23.0",
  "org.scodec" %% "scodec-bits" % "1.1.7",
  "org.scalatest" %% "scalatest" % "3.0.+" % "test",
  "org.scalacheck" %% "scalacheck" % "1.14.+" % "test"
)
