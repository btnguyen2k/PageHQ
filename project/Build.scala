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
    "com.google.guava" % "guava" % "14.0",
    "com.github.ddth" % "spring-social-helper" % "0.2-SNAPSHOT",
    "com.github.ddth" %% "play-module-plommon" % "0.3.2-SNAPSHOT",
    "com.typesafe" %% "play-plugins-redis" % "2.1.1",
    javaCore,
    javaJdbc,
    cache
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    resolvers += "Sonatype snapshots repository" at "http://oss.sonatype.org/content/repositories/snapshots/",
    resolvers += "pk11-scratch" at "http://pk11-scratch.googlecode.com/svn/trunk"
  )

}
