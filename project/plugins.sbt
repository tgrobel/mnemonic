// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.4")

addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "0.99.7.1")

addSbtPlugin("org.scoverage" %% "sbt-coveralls" % "0.99.0")