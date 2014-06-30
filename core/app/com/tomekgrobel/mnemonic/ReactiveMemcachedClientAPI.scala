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

import scala.concurrent.duration.Duration
import scala.concurrent.duration.Duration._
import shade.memcached.Codec
import scala.concurrent.Future


/**
 * Trait based on [[shade.memcached.Memcached Memcached]], from internally used client.
 * It is needed to cover [[shade.memcached.Memcached#close() close()]] method.
 * <br>
 * Additionally it defines default expiration time as an infinity.
 */
trait ReactiveMemcachedClientAPI {

  /**
   * Adds a value for a given key, if the key doesn't already exist in the cache store.
   *
   * If the key already exists in the cache, the future returned result will be false and the
   * current value will not be overridden. If the key isn't there already, the value
   * will be set and the future returned result will be true.
   *
   * The expiry time has default value of Duration.Inf (infinite duration).
   *
   * @return either true, in case the value was set, or false otherwise
   */
  def add[T](key: String, value: T, exp: Duration = Inf)(implicit codec: Codec[T]): Future[Boolean]

  /**
   * Sets a (key, value) in the cache store.
   *
   * The expiry time has default value of Duration.Inf (infinite duration).
   */
  def set[T](key: String, value: T, exp: Duration = Inf)(implicit codec: Codec[T]): Future[Unit]

  /**
   * Deletes a key from the cache store.
   *
   * @return true if a key was deleted or false if there was nothing there to delete
   */
  def delete(key: String): Future[Boolean]

  /**
   * Fetches a value from the cache store.
   *
   * @return Some(value) in case the key is available, or None otherwise (doesn't throw exception on key missing)
   */
  def get[T](key: String)(implicit codec: Codec[T]): Future[Option[T]]

  /**
   * Compare and set.
   *
   * @param expecting should be None in case the key is not expected, or Some(value) otherwise
   * @param exp The expiry time has default value of Duration.Inf (infinite)
   * @return either true (in case the compare-and-set succeeded) or false otherwise
   */
  def compareAndSet[T](key: String, expecting: Option[T], newValue: T, exp: Duration = Inf)(implicit codec: Codec[T]): Future[Boolean]

  /**
   * Transforms the given key and returns the new value.
   *
   * The cb callback receives the current value
   * (None in case the key is missing or Some(value) otherwise)
   * and should return the new value to store.
   *
   * The method retries until the compare-and-set operation succeeds, so
   * the callback should have no side-effects.
   *
   * This function can be used for atomic increments and stuff like that.
   *
   * @return the new value
   */
  def transformAndGet[T](key: String, exp: Duration = Inf)(cb: Option[T] => T)(implicit codec: Codec[T]): Future[T]

  /**
   * Transforms the given key and returns the old value as an Option[T]
   * (None in case the key wasn't in the cache or Some(value) otherwise).
   *
   * The cb callback receives the current value
   * (None in case the key is missing or Some(value) otherwise)
   * and should return the new value to store.
   *
   * The method retries until the compare-and-set operation succeeds, so
   * the callback should have no side-effects.
   *
   * This function can be used for atomic increments and stuff like that.
   *
   * @return the old value
   */
  def getAndTransform[T](key: String, exp: Duration = Inf)(cb: Option[T] => T)(implicit codec: Codec[T]): Future[Option[T]]

}
