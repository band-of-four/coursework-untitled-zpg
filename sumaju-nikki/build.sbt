name := "sumaju-nikki"
organization := "b4"

version := "1.0.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.7"

/* Database */
libraryDependencies ++= Seq(
  jdbc,
  evolutions,
  "org.postgresql" % "postgresql" % "42.2.5",
  "io.getquill" %% "quill-jdbc" % "2.5.4"
)

/* Testing */
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
