import Dependencies._

lazy val root = (project in file("."))
  .settings(
    inThisBuild(List(
      organization := "com.ruchij",
      scalaVersion := "2.12.7"
    )),
    name := "$name;format="normalize"$",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      pegdown % Test
    ),
    buildInfoKeys := BuildInfoKey.ofN(name, scalaVersion, sbtVersion),
    buildInfoPackage := "com.eed3si9n.ruchij",
    assemblyJarName in assembly := "$name;format="normalize"$-assembly.jar"
  )

enablePlugins(BuildInfoPlugin)

testOptions in Test +=
  Tests.Argument(TestFrameworks.ScalaTest, "-h", "target/test-results")

addCommandAlias("testWithCoverage", "; clean; coverage; test; coverageReport")
