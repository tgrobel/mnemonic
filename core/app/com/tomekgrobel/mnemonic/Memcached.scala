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

import play.api._
import scala.language.implicitConversions
import com.tomekgrobel.mnemonic.client.ReactiveMemcachedClientAPI

/**
 * Object to easy access reactive Memcached client [[com.tomekgrobel.mnemonic.client.ReactiveMemcachedClientAPI API]].
 * <br>
 * All methods delegates to API, and can be used directly from object.
 */
object Memcached {

  /**
   * Implicit function to effortless delegate to actual reactive Memcached client
   * [[com.tomekgrobel.mnemonic.client.ReactiveMemcachedClientAPI API]].
   * <br>
   * ===It should not be used directly.===
   * @param o object to add API method to
   * @param app instance of Play application
   * @return instance of reactive Memcached client API
   */
  implicit def delegateAPI(o: Memcached.type)(implicit app: Application): ReactiveMemcachedClientAPI = {
    app.plugin[MnemonicPlugin].getOrElse {
      throw new MnemonicPluginNotRegistered
    }.client
  }

}
