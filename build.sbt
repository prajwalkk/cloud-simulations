name := "Prajwal_Kammardi_HW1"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  // https://mvnrepository.com/artifact/org.scalatest/scalatest
  "org.scalatest" %% "scalatest" % "3.1.1" % Test,

  // https://mvnrepository.com/artifact/com.typesafe/config
  "com.typesafe" % "config" % "1.3.4",

  // https://mvnrepository.com/artifact/org.cloudsimplus/cloudsim-plus
  "org.cloudsimplus" % "cloudsim-plus" % "5.4.3",

  // https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
  "ch.qos.logback" % "logback-classic" % "1.2.3" % Test,

  // https://mvnrepository.com/artifact/com.typesafe.scala-logging/scala-logging
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",

  "com.github.pureconfig" %% "pureconfig" % "0.13.0",

  "org.slf4j" % "slf4j-api" % "1.7.5",

  "org.slf4j" % "slf4j-simple" % "1.7.5"

)