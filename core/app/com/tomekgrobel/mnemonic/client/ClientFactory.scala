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

import shade.memcached.{MemcachedImpl => ShadeMemcachedImpl, Memcached => ShadeMemcached, FakeMemcached}
import scala.concurrent.{ExecutionContextExecutor, ExecutionContext}
import shade.memcached.Configuration
import scala.Some

/**
 * Memcached client factory.
 */
class ClientFactory(clientConfigurationReader: ClientConfigurationReader,
                    realClientFactory: RealClientFactory = ClientFactory.realClient(),
                    fakeClientFactory: FakeClientFactory = ClientFactory.fakeClient())
                   (implicit ec: ExecutionContextExecutor) {

  /**
   * Creates new instance of Memcached client.
   * @return new instance of Memcached client.
   */
  def create: ShadeMemcached with ReactiveMemcachedClientAPI = {
    clientConfigurationReader.readClientConfiguration match {
      case Some(configuration) => realClientFactory.create(configuration, ec)
      case None => fakeClientFactory.create(ec)
    }
  }
}

object ClientFactory {

  def apply(clientConfigurationReader: ClientConfigurationReader)(implicit ec: ExecutionContextExecutor): ClientFactory = {
    new ClientFactory(clientConfigurationReader)
  }

  private def realClient(): RealClientFactory = {
    new RealClientFactory
  }

  private def fakeClient(): FakeClientFactory = {
    new FakeClientFactory
  }

}

/**
 * Wrapper to external class constructor.
 */
class RealClientFactory {
  def create(configuration: Configuration, ec: ExecutionContext): ShadeMemcached with ReactiveMemcachedClientAPI = {
    new ShadeMemcachedImpl(configuration, ec) with ReactiveMemcachedClientAPI
  }
}

/**
 * Wrapper to external class constructor.
 */
class FakeClientFactory {
  def create(ec: ExecutionContext): ShadeMemcached with ReactiveMemcachedClientAPI = {
    new FakeMemcached(ec) with ReactiveMemcachedClientAPI
  }
}
