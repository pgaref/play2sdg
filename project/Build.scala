import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "play2sdg"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "org.apache.lucene" % "lucene-analyzers" % "3.6.0",
    "com.datastax.cassandra" % "cassandra-driver-core" % "2.1.8",
    "com.datastax.cassandra" % "cassandra-driver-mapping" % "2.1.8",
    //"org.apache.lucene" % "lucene-analyzers-common" % "4.0.0",
    javaCore
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
      //Kundera Public repositories
      ebeanEnabled := false,
      resolvers += "Riptano" at "http://mvn.riptano.com/content/repositories/public",
      resolvers += "Scale 7" at "https://github.com/s7/mvnrepo/raw/master"
  )

}
