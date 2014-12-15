organization := "io.github.benwhitehead.scala"

name := "finch-experimentation"

version := "0.2-SNAPSHOT"

scalaVersion := "2.10.3"

scalacOptions ++= Seq("-unchecked", "-deprecation")

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

javacOptions in doc := Seq("-source", "1.7")

resolvers += "Twitter" at "http://maven.twttr.com/"

resolvers += "Finch.io" at "http://repo.konfettin.ru"

resolvers += "finch-server" at "http://storage.googleapis.com/benwhitehead_me/maven/public"

libraryDependencies ++= Seq(
  "io.github.benwhitehead.finch" %% "finch-server" % "0.5.0",
  "ch.qos.logback" % "logback-classic" % "1.1.2"
)

parallelExecution in Test := true

