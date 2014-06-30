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

import shade.memcached.{MemcachedImpl => ShadeMemcachedImpl, Memcached => ShadeMemcached, Configuration}
import scala.concurrent.ExecutionContext.Implicits.{global => ec}


/**
 * Memcached client factory.
 */
class ClientFactory {

  /**
   * Creates new instance of Memcached client.
   * @return new instance of Memcached client.
   */
  def newInstance: ShadeMemcached with ReactiveMemcachedClientAPI = {
    new ShadeMemcachedImpl(Configuration("127.0.0.1:11211"), ec) with ReactiveMemcachedClientAPI
  }
}
