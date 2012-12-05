name := "Scalex CLI"

version := "0.2"

libraryDependencies ++= Seq(
  "net.databinder" %% "dispatch-http" % "0.8.8",
  "net.liftweb" %% "lift-json" % "2.5-M3"
)

seq(ProguardPlugin.proguardSettings :_*)

proguardOptions ++= Seq(
  "-keep class org.apache.commons.logging.** { *; }",
  "-keep class scalex.cli.** { *; }",
  keepMain("scalex.cli.ScalexCLI")
)
