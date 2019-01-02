name := "sumaju-nikki"
organization := "b4"

version := "1.0.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"

resolvers += "Atlassian Releases" at "https://maven.atlassian.com/public/"

/* Database */
libraryDependencies ++= Seq(
  jdbc,
  evolutions,
  "org.postgresql" % "postgresql" % "42.2.5",
  "io.getquill" %% "quill-jdbc" % "2.6.0"
)

/* Auth */
val silhouetteVersion = "5.0.6"
libraryDependencies ++= Seq(
  "com.mohiva" %% "play-silhouette" % silhouetteVersion,
  "com.mohiva" %% "play-silhouette-persistence" % silhouetteVersion,
  "com.mohiva" %% "play-silhouette-password-bcrypt" % silhouetteVersion,
  "com.mohiva" %% "play-silhouette-crypto-jca" % silhouetteVersion
)

/* Configuration */
libraryDependencies += "com.iheart" %% "ficus" % "1.4.3"

/* Testing */
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += "io.lemonlabs" %% "scala-uri" % "1.4.0" % Test
