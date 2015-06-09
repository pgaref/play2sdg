import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "play2sdg"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here - Kundera Latest PLAY compatible version = 2.5 !
    "com.impetus.client" % "kundera-cassandra" % "2.9",
    "org.apache.lucene" % "lucene-analyzers" % "3.6.0",
    //"com.datastax.cassandra" % "cassandra-driver-core" % "2.0.3",
    //"org.apache.lucene" % "lucene-analyzers-common" % "4.0.0",
    javaCore
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
      //Kundera Public repositories
      ebeanEnabled := false,
      resolvers += "Kundera" at "https://oss.sonatype.org/content/repositories/releases",
      resolvers += "Riptano" at "http://mvn.riptano.com/content/repositories/public",
      resolvers += "Kundera missing" at "http://kundera.googlecode.com/svn/maven2/maven-missing-resources",
      resolvers += "Scale 7" at "https://github.com/s7/mvnrepo/raw/master"
  )

}
