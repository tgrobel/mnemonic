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

package com.tomekgrobel.mnemonic

import play.api.{Logger, Plugin, Application}
import shade.memcached.{Memcached => ShadeMemcached}
import com.tomekgrobel.mnemonic.client.{ClientConfigurationReader, ReactiveMemcachedClientAPI, ClientFactory}
import scala.concurrent.ExecutionContext.Implicits.{global => ec}
import play.api.Play.current
import MnemonicPlugin._

/**
 * Core class of reactive Memcached client plugin.
 * <br>
 * It should be registered in application configuration.
 */
class MnemonicPlugin(app: Application, clientFactory: ClientFactory) extends Plugin {

  /**
   * Additional constructor for instantiate plugin by Play framework.
   * @param app application
   */
  def this(app: Application) {
    this(app, defaultClientFactory)
  }

  private type MemcachedClient = ShadeMemcached with ReactiveMemcachedClientAPI

  private lazy val logger = Logger("mnemonic")

  private var clientInstance: Option[MemcachedClient] = None

  private def getClientInstance: MemcachedClient = {
    clientInstance.getOrElse {
      throw new MemcachedClientNotInstantiated
    }
  }

  /**
   * Access to instantiated Memcached client.
   * @return instantiated Memcached client
   */
  def client: ReactiveMemcachedClientAPI = {
    getClientInstance
  }

  /**
   * Instantiation of Memcached client.
   */
  override def onStart() {
    clientInstance = Some(clientFactory.create)
    logger.info("Mnemonic plugin initialization completed.")
  }

  /**
   * Closing Memcached client.
   */
  override def onStop() {
    logger.info("Stopping Mnemonic plugin...")
    logger.info("Closing Memcached client...")
    getClientInstance.close()
    logger.info("Mnemonic plugin stopped successfully.")
  }

}

object MnemonicPlugin {
  private def defaultClientFactory = ClientFactory(ClientConfigurationReader(play.api.Play.configuration))
}

/**
 * Thrown to indicate that the Mnemonic plugin is not registered in the application.
 */
class MnemonicPluginNotRegistered() extends RuntimeException(
  "Oops! Looks like Mnemonic plugin is not registered in the application!")

/**
 * Thrown to indicate that the Memcached client is not instantiated.
 */
class MemcachedClientNotInstantiated() extends RuntimeException(
  "Oops! Memcached client is not instantiated!")



