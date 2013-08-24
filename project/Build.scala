import sbt._
import Keys._
import play.Project._
import com.typesafe.config._

object ApplicationBuild extends Build {

  val conf = ConfigFactory.parseFile(new File("conf/application.conf")).resolve()
  val appName         = conf.getString("app.name")
  val appVersion      = conf.getString("app.version")

  val appDependencies = Seq(
    // Add your project dependencies here,
    "mysql" % "mysql-connector-java" % "5.1.26",
    "com.github.ddth" %% "play-module-plommon" % "0.1.0-SNAPSHOT",
    
    javaCore,
    javaJdbc
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    resolvers += "Sonatype snapshots repository" at "http://oss.sonatype.org/content/repositories/snapshots/"
  )

}
