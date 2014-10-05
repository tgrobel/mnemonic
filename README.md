Mnemonic - Reactive Memcached Module for Scala Play 2.3
=======================================================
[![Build Status](http://tomekgrobel.com/shippable/badge.php?project=542dbeb080088cee586d308b&branch=master)](https://app.shippable.com/projects/542dbeb080088cee586d308b/builds/latest) [![Coverage Status](https://img.shields.io/coveralls/tgrobel/mnemonic.svg?style=flat-square)](https://coveralls.io/r/tgrobel/mnemonic?branch=master) ![Latest Release](http://img.shields.io/maven-central/v/com.tomekgrobel/mnemonic.svg?style=flat-square)&nbsp;![Scala Version](http://img.shields.io/badge/scala-v2.11.x-DC322F.svg?style=flat-square)&nbsp;![Play Version](http://img.shields.io/badge/play_framework-v2.3.x-brightgreen.svg?style=flat-square)

Module that brings asynchronous communication with [Memcached] (http://memcached.org/) service to Scala Play 2.3 applications. 

It is compiled with `Scala 2.11`. Internally it uses [Shade](https://github.com/alexandru/shade) - reactive Memcached client for Scala. 


## Configuration

**Using latest release:**

Add to your dependencies:

```scala
"com.tomekgrobel.mnemonic" %% "mnemonic" % "0.1"
```

**Using current snapshot:**

Add snapshot resolver:

```scala
resolvers += "Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
```

Add to your dependencies:

```scala
"com.tomekgrobel.mnemonic" %% "mnemonic" % "master-SNAPSHOT"
```

**Common configuration:**

Add plugin to the `play.plugins` file:

```scala
1000:com.tomekgrobel.mnemonic.MnemonicPlugin
```

Create `mnemonic.conf` file in your app configuration directory (see below for details) and include it in `application.conf` file:

```scala
include "mnemonic.conf"
```

Import object in your scala files and use its API:

```scala
import com.tomekgrobel.mnemonic.Memcached

Memcached.set[String]("key", "message")
```

## Configuration file

Mnemonic configuration file is a normal Play configuration file. Here you can see all options configured:

```scala
mnemonic {
    addresses = "localhost:11211 localhost:22122"
    authentication {
        username = user
        password = pass
    }
    keysPrefix = prefix
    protocol = Binary
    failureMode = Retry
    operationTimeout = 500ms
}
# mnemonic.mock = true
```

### `mnemonic.addresses` *- required String*
List of Memcached service addresses, separated with space.


### `mnemonic.authentication` *- optional*
Authentication credentials for Memcached service if needed.


### `mnemonic.keysPrefix` *- optional String*
String that will be used as a prefix for all keys stored by your app in Memcached service.


### `mnemonic.protocol` *- optional String*
Protocol for storing data to Memcached service. Two values are available
* `Binary` - binary protocol (set by default)
* `Text` - text protocol

### `mnemonic.failureMode` *- optional String*
Specifies failure mode when connection drop. Three options are available
* `Retry` - connection is retried until it recovers (set by default)
* `Cancel` - all operations are cancelled
* `Redistribute` - the client tries to redistribute operations to other nodes

### `mnemonic.operationTimeout` *- optional FiniteDuration in milliseconds*
Specifies operation timeout. When the limit is reached, the Future responses finish with Failure(TimeoutException).
Default value is `1000ms`.

### `mnemonic.mock` *- optional Boolean*
Indicates the plugin mock mode
* `false` - the plugin will connect to the configured Memcached service (set by default)
* `true` - simple in-memory implementation of cache is used  

## API

Setting key and its value
```scala
import com.tomekgrobel.mnemonic.Memcached

val response: Future[Unit] = Memcached.set[String]("key", "value")
```
Adding value only if key does not exist in cache
```scala
import com.tomekgrobel.mnemonic.Memcached

val response: Future[Boolean] = Memcached.add[String]("key", "value")
```
Deleting key
```scala
import com.tomekgrobel.mnemonic.Memcached

val response: Future[Boolean] = Memcached.delete("key")
```
Fetching value
```scala
import com.tomekgrobel.mnemonic.Memcached

val response: Future[Option[String]] = Memcached.get[String]("key")
```

There is also a **compare-and-set** functionality. For more details please check [Shade client documentation](https://github.com/alexandru/shade#compare-and-set).
 
## Complex serialization and deserialization
It is realised by the internal used Memcached client. For more details please check [Shade client documentation](https://github.com/alexandru/shade#serializingdeserializing). 

## License

Mnemonic is published under the Apache License v2.0.

You may obtain a copy of the License at [http://www.apache.org/licenses/LICENSE-2.0] (http://www.apache.org/licenses/LICENSE-2.0).

<hr>
If you have any ideas how to improve this module or if you found an error please [create an issue](https://github.com/tgrobel/mnemonic/issues). You can also contact me on Twitter [@tgrobel](https://twitter.com/tgrobel). 
