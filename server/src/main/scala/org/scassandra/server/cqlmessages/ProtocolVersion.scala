/*
 * Copyright (C) 2014 Christopher Batey and Dogan Narinc
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
 */
package org.scassandra.server.cqlmessages

import CqlProtocolHelper._

import akka.util.{ByteStringBuilder, ByteIterator}

object ProtocolVersion {
  val ServerProtocolVersionThree : Byte = (0x81 & 0xFF).toByte
  val ClientProtocolVersionThree : Byte = 0x03

  val ServerProtocolVersionTwo : Byte = (0x82 & 0xFF).toByte
  val ClientProtocolVersionTwo : Byte = 0x02

  val ServerProtocolVersionOne : Byte = (0x81 & 0xFF).toByte
  val ClientProtocolVersionOne : Byte = 0x01


  def protocol(clientVersion: Byte) = {
    clientVersion match {
      case VersionThree.clientCode | VersionThree.serverCode => VersionThree
      case VersionTwo.clientCode | VersionTwo.serverCode => VersionTwo
      case VersionOne.clientCode | VersionOne.serverCode => VersionOne
    }
  }
}

/**
 * Defines a set of functions used to retrieve and apply length
 * on a byte stream.
 */
trait CollectionLength {
  def getLength(iterator: ByteIterator): Int
  def putLength(bs: ByteStringBuilder, length: Int): ByteStringBuilder
}

case object ShortLength extends CollectionLength {
  override def getLength(iterator: ByteIterator) = iterator.getShort
  override def putLength(bs: ByteStringBuilder, length: Int) = bs.putShort(length)
}

case object IntLength extends CollectionLength {
  override def getLength(iterator: ByteIterator) = iterator.getInt
  override def putLength(bs: ByteStringBuilder, length: Int) = bs.putInt(length)
}

abstract class ProtocolVersion(val clientCode : Byte, val serverCode: Byte, val version: Int, val collectionLength: CollectionLength = IntLength)

case object VersionThree extends ProtocolVersion(
  ProtocolVersion.ClientProtocolVersionThree,
  ProtocolVersion.ServerProtocolVersionThree,
  3)

case object VersionTwo extends ProtocolVersion(
  ProtocolVersion.ClientProtocolVersionTwo,
  ProtocolVersion.ServerProtocolVersionTwo,
  2,
  collectionLength = ShortLength)

case object VersionOne extends ProtocolVersion(
  ProtocolVersion.ClientProtocolVersionOne,
  ProtocolVersion.ServerProtocolVersionOne,
  1,
  collectionLength = ShortLength)
