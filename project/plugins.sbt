// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("play" % "sbt-plugin" % "2.1.5")

// eclipse plugin -- use the command: sbt eclipse
// alternative option: play command and: eclipse with-source=true
//addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.1.5")

//unmanagedJars = ( baseDirectory / "target/scala-2.10/classes" )