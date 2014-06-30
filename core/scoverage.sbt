instrumentSettings

ScoverageKeys.highlighting := true

// exclude wrappers to external class constructors
ScoverageKeys.excludedPackages in ScoverageCompile := ".*FakeClientFactory;.*RealClientFactory"