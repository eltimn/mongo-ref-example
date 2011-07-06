import sbt._
import Process._

import untyped.ClosureCompilerPlugin

class LiftProject(info: ProjectInfo) extends DefaultWebProject(info) with ClosureCompilerPlugin {
  lazy val isAutoScan = systemOptional[Boolean]("autoscan", false).value
  val liftVersion = "2.4-M1"

  // uncomment the following if you want to use the snapshot repo
  //val scalatoolsSnapshot = ScalaToolsSnapshots

  override def classpathFilter = super.classpathFilter -- "*-sources.jar"
  override def scanDirectories = if (isAutoScan) super.scanDirectories else Nil

  // Lift
  lazy val lift_mongodb = "net.liftweb" %% "lift-mongodb-record" % liftVersion
  lazy val lift_wizard = "net.liftweb" %% "lift-wizard" % liftVersion

  // misc
  lazy val jbcrypt = "org.mindrot" % "jbcrypt" % "0.3m"
  lazy val logback = "ch.qos.logback" % "logback-classic" % "0.9.26"

  // test-scope
	lazy val specs = "org.scala-tools.testing" %% "specs" % "1.6.8" % "test->default"
	lazy val jetty6 = "org.mortbay.jetty" % "jetty" % "6.1.22" % "test->default"

	// google-closure plugin
	override def closureSourcePath: Path = "src" / "main" / "javascript"

	lazy val deploy = task {
    "vmc update lift-mongo-example --path target/scala_2.9.0-1/" ! log
    None
  } dependsOn(packageAction) describedAs("Deploy war to production.")

	// Initialize Boot by default
  override def consoleInit =
    """
      |import bootstrap.liftweb.Boot
      |
      |val b = new Boot
      |b.boot
      |
    """.stripMargin
}
