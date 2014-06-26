name := "mnemonic"

version := Common.version

organization := Common.organization

scalaVersion := Common.scalaVersion

libraryDependencies ++= Seq(
    "com.bionicspirit" %% "shade" % "1.6.0"
)

resolvers += "SpyMemcached" at "http://files.couchbase.com/maven2/"

