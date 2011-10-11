name := "Scalex CLI"

version := "0.1"

libraryDependencies ++= Seq(
  "net.databinder" %% "dispatch-http" % "0.8.5",
  "net.liftweb" %% "lift-json" % "2.4-M4"
)

seq(ProguardPlugin.proguardSettings :_*)

proguardOptions ++= Seq(
  "-keep class org.apache.commons.logging.** { *; }",
  "-keep class scalex.cli.** { *; }",
  keepMain("scalex.cli.ScalexCLI")
)
