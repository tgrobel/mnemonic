/**
 * Copyright 2014 Tomasz Grobel
 * tgrobel@gmail.com
 * twitter: @tgrobel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tomekgrobel.mnemonic.client

import org.scalatest.{Matchers, FunSpec}
import com.typesafe.config.ConfigFactory
import play.api.Configuration
import ClientConfigurationReaderTest._
import scala.language.implicitConversions
import concurrent.duration._
import shade.memcached.{Protocol => ProtocolEnumeration, FailureMode => FailureModeEnumeration, Configuration => ClientConfiguration, AuthConfiguration}

class ClientConfigurationReaderTest extends FunSpec {

  describe("A ClientConfigurationReader") {
    describe("when mock is configured") {

      val reader: ClientConfigurationReader = new ClientConfigurationReader(someConfigurationWithMock)

      it("should not return configuration") {
        assert(reader.readClientConfiguration == None)
      }
    }

    describe("when full configuration is available") {

      val reader: ClientConfigurationReader = new ClientConfigurationReader(someFullConfiguration)

      it("should return full configuration") {
        assert(reader.readClientConfiguration == expectedFullConfiguration)
      }
    }

    describe("when minimal configuration is available") {

      val reader: ClientConfigurationReader = new ClientConfigurationReader(someMinimalConfiguration)

      it("should return minimal configuration with defaults") {
        assert(reader.readClientConfiguration == expectedMinimalConfigurationWithDefaults)
      }
    }

    describe("when configuration with missing addresses is available") {

      val reader: ClientConfigurationReader = new ClientConfigurationReader(someConfigurationWithMissingAddresses)

      it("should throw MnemonicPluginMemcachedConfigurationKeyMissing exception") {
        val exception = intercept[MnemonicPluginMemcachedConfigurationKeyMissing] {
          reader.readClientConfiguration
        }
        assert(exception.getMessage.contains("addresses"), "Exception caused by other key!")
      }
    }

    describe("when configuration with authentication is available but password is missing") {

      val reader: ClientConfigurationReader = new ClientConfigurationReader(someConfigurationWithMissingPassword)

      it("should throw MnemonicPluginMemcachedConfigurationKeyMissing exception") {
        val exception = intercept[MnemonicPluginMemcachedConfigurationKeyMissing] {
          reader.readClientConfiguration
        }
        assert(exception.getMessage.contains("password"), "Exception caused by other key!")
      }
    }

    describe("when configuration with authentication is available but user is missing") {

      val reader: ClientConfigurationReader = new ClientConfigurationReader(someConfigurationWithMissingUser)

      it("should throw MnemonicPluginMemcachedConfigurationKeyMissing exception") {
        val exception = intercept[MnemonicPluginMemcachedConfigurationKeyMissing] {
          reader.readClientConfiguration
        }
        assert(exception.getMessage.contains("user"), "Exception caused by other key!")
      }
    }

    describe("when no plugin configuration is available") {

      it("should throw MnemonicPluginConfigurationMissing exception") {
        intercept[MnemonicPluginConfigurationMissing] {
          new ClientConfigurationReader(someConfigurationWithMissingPlugin)
        }
      }
    }

  }

}

object ClientConfigurationReaderTest {

  implicit def toConfiguration(conf: String): Configuration = {
    Configuration(ConfigFactory.parseString(conf))
  }

  val someConfigurationWithMock = "mnemonic.mock = true"

  val someFullConfiguration =
    """
      |mnemonic {
      | addresses = "localhost:12345 192.168.0.1:12345"
      | authentication {
      |   username = user
      |   password = pass
      | }
      | keysPrefix = prefix
      | protocol = Binary
      | failureMode = Retry
      | operationTimeout = 500ms
      |}
    """.stripMargin

  val expectedFullConfiguration = Some(ClientConfiguration("localhost:12345 192.168.0.1:12345",
    Some(AuthConfiguration("user", "pass")), Some("prefix"), ProtocolEnumeration.Binary,
    FailureModeEnumeration.Retry, 500.milliseconds))

  val someMinimalConfiguration = """mnemonic.addresses = "localhost:12345""""

  val expectedMinimalConfigurationWithDefaults = Some(ClientConfiguration("localhost:12345", None, None,
    ProtocolEnumeration.Binary, FailureModeEnumeration.Retry, 1000.milliseconds))

  val someConfigurationWithMissingAddresses = "mnemonic {}"

  val someConfigurationWithMissingPassword =
    """
      |mnemonic.authentication.username = user
      |mnemonic.addresses = "localhost:12345"
    """.stripMargin

  val someConfigurationWithMissingUser =
    """
      |mnemonic.addresses = "localhost:12345"
      |mnemonic.authentication.password = pass
    """.stripMargin

  val someConfigurationWithMissingPlugin = "missing = configuration"

}
