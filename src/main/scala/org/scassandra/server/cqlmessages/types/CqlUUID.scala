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
package org.scassandra.server.cqlmessages.types

import java.util.UUID
import akka.util.ByteIterator
import org.apache.cassandra.serializers.{TypeSerializer}
import org.scassandra.server.cqlmessages.{ProtocolVersion, CqlProtocolHelper}
import org.scassandra.server.cqlmessages.types.CustomUUIDSerializer

case object CqlUUID extends ColumnType[UUID](0x000C, "uuid") {
   override def readValue(byteIterator: ByteIterator, protocolVersion: ProtocolVersion): Option[UUID] = {
     CqlProtocolHelper.readUUIDValue(byteIterator)
   }

   def writeValue( value: Any) = {
     CqlProtocolHelper.serializeUUIDValue(UUID.fromString(value.toString))
   }

  override def convertToCorrectCollectionTypeForList(list: Iterable[_]) : List[UUID] = {
    list.map {
      case bd: String => UUID.fromString(bd)
      case uuid: UUID => uuid
      case _ => throw new IllegalArgumentException("Expected string representing an uuid")
    }.toList
  }

  override def serializer: TypeSerializer[UUID] = CustomUUIDSerializer.instance
 }