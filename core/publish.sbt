credentials += Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", System.getenv("SONATYPE_USER"), System.getenv("SONATYPE_PASS"))

publishMavenStyle := true

publishArtifact in Test := false

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

organizationName := "Tomasz Grobel"

organizationHomepage := Some(new URL("http://tomekgrobel.com"))

startYear := Some(2014)

description := "Reactive Memcached Module for Scala Play Framework."

licenses := Seq("The Apache Software License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

homepage := Some(url("https://github.com/tgrobel/mnemonic"))

pomIncludeRepository := { _ => false }

pomExtra := (
  <scm>
    <url>git@github.com:tgrobel/mnemonic.git</url>
    <connection>scm:git:git@github.com:tgrobel/mnemonic.git</connection>
  </scm>
  <developers>
    <developer>
      <id>tgrobel</id>
      <name>Tomasz Grobel</name>
      <url>http://tomekgrobel.com</url>
    </developer>
  </developers>)