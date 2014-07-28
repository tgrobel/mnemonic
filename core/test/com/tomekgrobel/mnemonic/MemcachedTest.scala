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

import org.scalatest.FunSpec
import play.api.test.{Helpers, FakeApplication}
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps
import MemcachedTest._

class MemcachedTest extends FunSpec {

  describe("A Memcached") {

    describe("when used in application started without plugin defined") {

      val app: FakeApplication = FakeApplication()

      it("should throw exception") {
        Helpers.running(app) {
          intercept[MnemonicPluginNotRegistered] {
            Memcached.add[String](key, value)
          }
        }
      }
    }

    describe("when used in application started with plugin defined") {

      val app: FakeApplication = FakeApplication(additionalPlugins = Seq("com.tomekgrobel.mnemonic.MnemonicPlugin"))

      def verifyThatCacheContains(value: String) {
        val result = Await.result(Memcached.get[String](key), atMost)
        assert(result === Some(value))
      }

      it("should be able to add value to Memcache") {
        Helpers.running(app) {
          val added = Await.result(Memcached.add[String](key, value), atMost)
          assert(added)
          verifyThatCacheContains(value)
        }
      }

      it("should be able to set new value to Memcache") {
        Helpers.running(app) {
          Await.result(Memcached.set[String](key, value), atMost)
          verifyThatCacheContains(value)
        }
      }

      it("should be able to remove value from Memcache") {
        Helpers.running(app) {
          Await.result(Memcached.set[String](key, value), atMost)
          val deleted = Await.result(Memcached.delete(key), atMost)
          assert(deleted)
        }
      }

      it("should be able to change value in Memcache if condition is fulfilled") {
        Helpers.running(app) {
          Await.result(Memcached.set[String](key, value), atMost)
          val changed = Await.result(Memcached.compareAndSet[String](key, Some(value), newValue), atMost)
          assert(changed)
        }
      }

      it("should not be able to change value in Memcache if condition is not fulfilled") {
        Helpers.running(app) {
          Await.result(Memcached.set[String](key, value), atMost)
          val changed = Await.result(Memcached.compareAndSet[String](key, None, newValue), atMost)
          assert(!changed)
        }
      }

      it("should be able to get value and transform it in Memcache") {
        Helpers.running(app) {
          Await.result(Memcached.set[String](key, value), atMost)
          val result = Await.result(Memcached.getAndTransform(key) {
            o: Option[String] => newValue
          }, atMost)
          assert(result === Some(value))
          verifyThatCacheContains(newValue)
        }
      }

      it("should be able to transform value and get it from Memcache") {
        Helpers.running(app) {
          Await.result(Memcached.set[String](key, value), atMost)
          val result = Await.result(Memcached.transformAndGet(key) {
            o: Option[String] => newValue
          }, atMost)
          assert(result === newValue)
          verifyThatCacheContains(newValue)
        }
      }

    }
  }

}

object MemcachedTest {

  val atMost = 5 seconds
  val key: String = "key"
  val value: String = "value"
  val newValue: String = "newValue"

}

