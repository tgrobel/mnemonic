name := "mnemonic"

version := Common.version

organization := Common.organization

scalaVersion := Common.scalaVersion

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

libraryDependencies ++= Seq(
    "com.bionicspirit" % "shade_2.11" % "1.6.0",
    "org.scalatest" % "scalatest_2.11" % "2.2.0" % "test",
    "org.pegdown" % "pegdown" % "1.4.2" % "test",
    "org.mockito" % "mockito-core" % "1.9.5" % "test"
)

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oDW", "-u", crossTarget.value + "/test-reports", "-h", crossTarget.value + "/test-reports-html")

resolvers += "SpyMemcached" at "http://files.couchbase.com/maven2/"
