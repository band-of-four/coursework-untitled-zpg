name := "sumaju-nikki"
organization := "b4"

version := "1.0.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.7"

resolvers += "Atlassian Releases" at "https://maven.atlassian.com/public/"

/* Database */
libraryDependencies ++= Seq(
  jdbc,
  evolutions,
  "org.postgresql" % "postgresql" % "42.2.5",
  "io.getquill" %% "quill-jdbc" % "2.5.4"
)

/* Auth */
libraryDependencies += "com.mohiva" %% "play-silhouette" % "5.0.6"

/* Testing */
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
