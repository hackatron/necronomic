import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "necronomic"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
    )

    def customLessEntryPoints(base: File): PathFinder = (
      (base / "app" / "assets" / "stylesheets" / "bootstrap" * "bootstrap.less") +++
      (base / "app" / "assets" / "stylesheets" / "bootstrap" * "responsive.less") +++
      (base / "app" / "assets" / "stylesheets" * "main.less")
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      lessEntryPoints <<= baseDirectory(customLessEntryPoints)
    )

}
