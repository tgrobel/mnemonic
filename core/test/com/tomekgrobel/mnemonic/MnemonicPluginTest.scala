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

import MnemonicPluginTest._
import org.scalatest.FunSpec
import play.api.Application
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar._
import shade.memcached.{Codec, Memcached => ShadeMemcached}
import scala.concurrent.duration.Duration
import scala.concurrent.Future
import com.tomekgrobel.mnemonic.client.{ReactiveMemcachedClientAPI, ClientFactory}

class MnemonicPluginTest extends FunSpec {

  describe("A MnemonicPlugin") {
    describe("when started") {

      val plugin: MnemonicPlugin = new MnemonicPlugin(someApplication, someClientFactory)
      plugin.onStart()

      it("should contain client instance") {
        assert(plugin.client == someClient)
      }

      it("should be able to be stopped") {
        plugin.onStop() // no exception expected
      }
    }

    describe("when not started") {

      val plugin: MnemonicPlugin = new MnemonicPlugin(someApplication, someClientFactory)

      it("should throw exception when asked for client instance") {
        intercept[MemcachedClientNotInstantiated] {
          plugin.client
        }
      }

      it("should throw exception when tried to be stopped") {
        intercept[MemcachedClientNotInstantiated] {
          plugin.onStop()
        }
      }
    }

  }
}

object MnemonicPluginTest {

  val someApplication = mock[Application]
  val someClient = new ShadeMemcached with ReactiveMemcachedClientAPI {
    def add[T](key: String, value: T, exp: Duration)(implicit codec: Codec[T]): Future[Boolean] = ???

    def set[T](key: String, value: T, exp: Duration)(implicit codec: Codec[T]): Future[Unit] = ???

    def delete(key: String): Future[Boolean] = ???

    def get[T](key: String)(implicit codec: Codec[T]): Future[Option[T]] = ???

    def compareAndSet[T](key: String, expecting: Option[T], newValue: T, exp: Duration)(implicit codec: Codec[T]): Future[Boolean] = ???

    def transformAndGet[T](key: String, exp: Duration)(cb: (Option[T]) => T)(implicit codec: Codec[T]): Future[T] = ???

    def getAndTransform[T](key: String, exp: Duration)(cb: (Option[T]) => T)(implicit codec: Codec[T]): Future[Option[T]] = ???

    def close(): Unit = {}
  }

  def someClientFactory = {
    val factory = mock[ClientFactory]
    when(factory.create).thenReturn(someClient)
    factory
  }
}
