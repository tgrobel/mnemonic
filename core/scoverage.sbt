// usage sbt scoverage:test / sbt coveralls

instrumentSettings

coverallsSettings

ScoverageKeys.highlighting := true

// exclude wrappers to external class constructors
ScoverageKeys.excludedPackages in ScoverageCompile := ".*FakeClientFactory;.*RealClientFactory"