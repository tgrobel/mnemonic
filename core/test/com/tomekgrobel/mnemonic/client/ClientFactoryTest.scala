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

import org.scalatest.FunSpec
import ClientFactoryTest._
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar.mock
import shade.memcached._
import scala.concurrent.{Future, ExecutionContextExecutor}
import scala.concurrent.duration.Duration
import shade.memcached.Configuration
import scala.Some

class ClientFactoryTest extends FunSpec {

  implicit val ec = someExecutionContextExecutor

  describe("A ClientFactory") {

    describe("when fake configuration provided") {

      val factory: ClientFactory = new ClientFactory(clientConfigurationReaderWithFakeConfiguration, realClientFactory, fakeClientFactory)

      it("should create fake client") {
        assert(factory.create == someFakeClient)
      }

    }

    describe("when real configuration provided") {

      val factory: ClientFactory = new ClientFactory(clientConfigurationReaderWithRealConfiguration, realClientFactory, fakeClientFactory)

      it("should create real client") {
        assert(factory.create == someRealClient)
      }

    }
  }

}

object ClientFactoryTest {

  val someExecutionContextExecutor = mock[ExecutionContextExecutor]

  def fakeClientFactory: FakeClientFactory = {
    val factory = mock[FakeClientFactory]
    when(factory.create(someExecutionContextExecutor)).thenReturn(someFakeClient)
    factory
  }

  def realClientFactory: RealClientFactory = {
    val factory = mock[RealClientFactory]
    when(factory.create(someConfiguration, someExecutionContextExecutor)).thenReturn(someRealClient)
    factory
  }

  def clientConfigurationReaderWithFakeConfiguration: ClientConfigurationReader = {
    val reader = mock[ClientConfigurationReader]
    when(reader.readClientConfiguration).thenReturn(None)
    reader
  }

  val someConfiguration: Configuration = mock[Configuration]

  def clientConfigurationReaderWithRealConfiguration: ClientConfigurationReader = {
    val reader = mock[ClientConfigurationReader]
    when(reader.readClientConfiguration).thenReturn(Some(someConfiguration))
    reader
  }

  val someFakeClient = new Memcached with ReactiveMemcachedClientAPI {
    def add[T](key: String, value: T, exp: Duration)(implicit codec: Codec[T]): Future[Boolean] = ???

    def set[T](key: String, value: T, exp: Duration)(implicit codec: Codec[T]): Future[Unit] = ???

    def delete(key: String): Future[Boolean] = ???

    def get[T](key: String)(implicit codec: Codec[T]): Future[Option[T]] = ???

    def compareAndSet[T](key: String, expecting: Option[T], newValue: T, exp: Duration)(implicit codec: Codec[T]): Future[Boolean] = ???

    def transformAndGet[T](key: String, exp: Duration)(cb: (Option[T]) => T)(implicit codec: Codec[T]): Future[T] = ???

    def getAndTransform[T](key: String, exp: Duration)(cb: (Option[T]) => T)(implicit codec: Codec[T]): Future[Option[T]] = ???

    def close(): Unit = ???
  }

  val someRealClient = new Memcached with ReactiveMemcachedClientAPI {
    def add[T](key: String, value: T, exp: Duration)(implicit codec: Codec[T]): Future[Boolean] = ???

    def set[T](key: String, value: T, exp: Duration)(implicit codec: Codec[T]): Future[Unit] = ???

    def delete(key: String): Future[Boolean] = ???

    def get[T](key: String)(implicit codec: Codec[T]): Future[Option[T]] = ???

    def compareAndSet[T](key: String, expecting: Option[T], newValue: T, exp: Duration)(implicit codec: Codec[T]): Future[Boolean] = ???

    def transformAndGet[T](key: String, exp: Duration)(cb: (Option[T]) => T)(implicit codec: Codec[T]): Future[T] = ???

    def getAndTransform[T](key: String, exp: Duration)(cb: (Option[T]) => T)(implicit codec: Codec[T]): Future[Option[T]] = ???

    def close(): Unit = ???
  }


}
