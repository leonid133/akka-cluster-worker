import AssemblyKeys._

assemblySettings

name := "worker-node"
version := "0.0.1"

//scalaVersion := "2.11.4"
//scalaVersion := "2.10.6"
/*
val versionRegex = "(\\d+)\\.(\\d+).*".r
val sparkVersion = util.Properties.propOrNone("sparkVersion").getOrElse("[1.5.2,)")

scalaVersion := {
  sparkVersion match {
    case versionRegex(major, minor) if major.toInt == 1 && List(4, 5, 6).contains(minor.toInt) => "2.10.6"
    case versionRegex(major, minor) if major.toInt > 1 => "2.11.8"
    case _ => "2.10.6"
  }
}*/

//scalaVersion := "2.10.6"
scalaVersion := "2.11.7"

crossScalaVersions := Seq("2.10.6", "2.11.8")

//val akkaVersion = "2.4-SNAPSHOT"
val akkaVersion = "2.4.0"

/*val akkaVersion ={
  scalaVersion.toString match {
    case "2.11.4" => "2.4.6"
    case "2.10.6" => "2.4-SNAPSHOT"
  }
}*/

val sparkVersion = "1.5.2"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-hive" % sparkVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion
  )

resolvers += "Akka Snapshots" at "http://repo.akka.io/snapshots/"
