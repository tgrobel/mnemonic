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

import org.scalatest.{Tag, FunSpec}
import play.api.test.{Helpers, FakeApplication}
import scala.concurrent.{Awaitable, Await}
import scala.concurrent.duration._
import scala.language.postfixOps
import MemcachedTest._
import play.api.Application

class MemcachedTest extends InAppFunSpec {

  describe("A Memcached API object") {

    describe("when used in application started without plugin defined") {

      implicit val app: FakeApplication = FakeApplication()

      in_app_it("should throw exception") {
        intercept[MnemonicPluginNotRegistered] {
          Memcached.add[String](key, value)
        }
      }
    }

    describe("when used in application started with plugin defined") {

      implicit val app: FakeApplication = FakeApplication(additionalPlugins = Seq("com.tomekgrobel.mnemonic.MnemonicPlugin"))

      in_app_it("should be able to add value to Memcached") {
        val added = Memcached.add[String](key, value).waitForResult
        assert(added)
        verifyThatCacheContains(value)
      }

      in_app_it("should be able to set new value to Memcached") {
        Memcached.set[String](key, value).waitForResult
        verifyThatCacheContains(value)
      }

      in_app_it("should be able to remove value from Memcached") {
        Memcached.set[String](key, value).waitForResult
        val deleted = Memcached.delete(key).waitForResult
        assert(deleted)
      }

      in_app_it("should be able to change value in Memcached if condition is fulfilled") {
        Memcached.set[String](key, value).waitForResult
        val changed = Memcached.compareAndSet[String](key, Some(value), newValue).waitForResult
        assert(changed)
      }

      in_app_it("should not be able to change value in Memcached if condition is not fulfilled") {
        Memcached.set[String](key, value).waitForResult
        val changed = Memcached.compareAndSet[String](key, None, newValue).waitForResult
        assert(!changed)
      }

      in_app_it("should be able to get value and transform it in Memcached") {
        Memcached.set[String](key, value).waitForResult
        val result = Memcached.getAndTransform(key) {
          o: Option[String] => newValue
        }.waitForResult
        assert(result === Some(value))
        verifyThatCacheContains(newValue)
      }

      in_app_it("should be able to transform value and get it from Memcached") {
        Memcached.set[String](key, value).waitForResult
        val result = Memcached.transformAndGet(key) {
          o: Option[String] => newValue
        }.waitForResult
        assert(result === newValue)
        verifyThatCacheContains(newValue)
      }

      def verifyThatCacheContains(value: String) {
        val result = Memcached.get[String](key).waitForResult
        assert(result === Some(value))
      }

    }
  }

}

object MemcachedTest {

  val atMost = 5 seconds
  val key: String = "key"
  val value: String = "value"
  val newValue: String = "newValue"

  implicit class AwaitSupport[T](awaitable: Awaitable[T]) {
    def waitForResult: T = {
      Await.result(awaitable, atMost)
    }
  }

}

trait InAppFunSpec extends FunSpec {

  protected val in_app_it = new ItWordInApp

  class ItWordInApp extends ItWord {
    def apply(specText: String, testTags: Tag*)(testFun: => Unit)(implicit app: Application) {
      super.apply(specText, testTags: _*) {
        Helpers.running(app)(testFun)
      }
    }
  }

}
