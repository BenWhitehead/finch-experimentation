organization := "io.github.benwhitehead.scala"

name := "finch-experimentation"

version := "0.1-SNAPSHOT"

scalaVersion := "2.10.3"

scalacOptions ++= Seq("-unchecked", "-deprecation")

javacOptions ++= Seq("-source", "1.6", "-target", "1.6")

javacOptions in doc := Seq("-source", "1.6")

resolvers += "Twitter" at "http://maven.twttr.com/"

resolvers += "Finch.io" at "http://repo.konfettin.ru"

resolvers += "BenWhitehead" at "http://storage.googleapis.com/benwhitehead_me/maven/public"

libraryDependencies ++= Seq(
  "io.github.benwhitehead.finch" %% "finch-server" % "0.4.1",
  "ch.qos.logback"  %   "logback-classic"   % "1.1.2"
)

parallelExecution in Test := true

