name := "CuckooScala"

version := "0.2.0"

scalaVersion := "2.12.8"

organization := "com.github.oskin1"

libraryDependencies ++= Seq(
  "com.google.guava" % "guava" % "23.0",
  "org.scodec" %% "scodec-bits" % "1.1.7",
  "org.scalatest" %% "scalatest" % "3.0.+" % "test",
  "org.scalacheck" %% "scalacheck" % "1.14.+" % "test",
  "com.storm-enroute" %% "scalameter" % "0.8.+" % "test"
)

licenses in ThisBuild := Seq("GNU GPL 3.0" -> url("https://github.com/oskin1/CuckooScala/blob/master/LICENSE"))

homepage in ThisBuild := Some(url("https://github.com/oskin1/CuckooScala"))

publishMavenStyle in ThisBuild := true

publishArtifact in Test := false

publishTo in ThisBuild :=
  Some(if (isSnapshot.value) Opts.resolver.sonatypeSnapshots else Opts.resolver.sonatypeStaging)

pomExtra in ThisBuild :=
  <scm>
    <url>git@github.com:oskin1/CuckooScala.git</url>
    <connection>scm:git:git@github.com:oskin1/CuckooScala.git</connection>
  </scm>
  <developers>
    <developer>
      <id>Oskin1</id>
      <name>Ilya Oskin</name>
    </developer>
  </developers>
