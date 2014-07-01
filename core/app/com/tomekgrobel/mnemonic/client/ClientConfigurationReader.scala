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

import shade.memcached.{Protocol => ProtocolEnumeration, FailureMode => FailureModeEnumeration, Configuration => ClientConfiguration, AuthConfiguration}
import concurrent.duration._
import ClientConfigurationReader._
import play.api.{Configuration => PlayConfiguration}

class ClientConfigurationReader(val globalConfig: PlayConfiguration) {

  private val mnemonicConfiguration = readMnemonicConfiguration()

  /**
   * Provides Memcached client configuration based on configuration file.
   * @return <code>Some</code> Memcached client configuration or <code>None</code> if mock is configured.
   */
  def readClientConfiguration: Option[ClientConfiguration] = {
    if (mockConfigured) {
      None
    } else {
      Some(ClientConfiguration(addresses, authentication, keysPrefix, protocol, failureMode, operationTimeout))
    }
  }

  private def addresses: String = {
    mnemonicConfiguration.getString(Addresses).getOrElse {
      throw new MnemonicPluginMemcachedConfigurationKeyMissing(Addresses)
    }
  }

  private def authentication: Option[AuthConfiguration] = {
    mnemonicConfiguration.getConfig(Authentication) match {
      case Some(authConfig) => {
        val username = authConfig.getString(Username).getOrElse(throw new MnemonicPluginMemcachedConfigurationKeyMissing(Username))
        val password = authConfig.getString(Password).getOrElse(throw new MnemonicPluginMemcachedConfigurationKeyMissing(Password))
        Some(AuthConfiguration(username, password))
      }
      case _ => None
    }
  }

  private def keysPrefix: Option[String] = {
    mnemonicConfiguration.getString(KeysPrefix)
  }

  private def protocol: ProtocolEnumeration.Value = {
    ProtocolEnumeration.withName(mnemonicConfiguration.getString(Protocol).getOrElse {
      ProtocolEnumeration.Binary.toString
    })
  }

  private def failureMode: FailureModeEnumeration.Value = {
    FailureModeEnumeration.withName(mnemonicConfiguration.getString(FailureMode).getOrElse {
      FailureModeEnumeration.Retry.toString
    })
  }

  private def operationTimeout: FiniteDuration = {
    mnemonicConfiguration.getMilliseconds(OperationTimeout).getOrElse(1000L).milliseconds
  }

  private def mockConfigured: Boolean = {
    mnemonicConfiguration.getBoolean(Mock).exists((value) => value)
  }

  private def readMnemonicConfiguration(): PlayConfiguration = {
    globalConfig.getConfig(MnemonicConfiguration).getOrElse {
      throw new MnemonicPluginConfigurationMissing
    }
  }

}

object ClientConfigurationReader {

  val MnemonicConfiguration = "mnemonic"
  val Mock = "mock"
  val Addresses = "addresses"
  val Authentication = "authentication"
  val Username = "username"
  val Password = "password"
  val KeysPrefix = "keysPrefix"
  val Protocol = "protocol"
  val FailureMode = "failureMode"
  val OperationTimeout = "operationTimeout"

  def apply(globalConfig: PlayConfiguration): ClientConfigurationReader = {
    new ClientConfigurationReader(globalConfig)
  }
}

/**
 * Thrown to indicate that the configuration is missing.
 */
class MnemonicPluginConfigurationMissing() extends RuntimeException(
  s"Oops! Looks like Mnemonic plugin configuration is missing! Please, verify if '$MnemonicConfiguration' path is available in configuration file!")

/**
 * Thrown to indicate that the Memcached addresses configuration is missing.
 */
class MnemonicPluginMemcachedConfigurationKeyMissing(key: String) extends RuntimeException(
  s"Oops! Looks like '$key' key is missing in Mnemonic plugin configuration!")


