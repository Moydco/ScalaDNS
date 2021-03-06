/**
 * Copyright 2013-2015, AlwaysResolve Project (alwaysresolve.org), MOYD.CO LTD
 * This file incorporates work covered by the following copyright and permission notice:
 *
 * Copyright 2012 silenteh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package payload

import io.netty.buffer.ByteBuf
import enums.RecordType
import org.slf4j.LoggerFactory
import scala.reflect.ClassTag

// This class reassemble the network frames
case class Message(
  header: Header,
  query: Array[Question],
  answers: Array[RRData],
  authority: Array[RRData],
  additional: Array[RRData]
) {
  override def toString() = {
    val opcodeStr = header.opcode match {
      case 0 => "Standard query"
      case 1 => "Inverse query"
      case 2 => "Status"
      case 4 => "Notify"
      case 5 => "Update"
    }
    "%s - %s - type: %s, class: %s".format(opcodeStr, domainName, RecordType(query(0).qtype).toString, query(0).qclass)
  }
  
  def toByteArray = header.toByteArray ++ 
    query.foldRight(Array[Byte]()){case(question, total) => question.toByteArray ++ total} ++ 
    answers.foldRight(Array[Byte]()){case(answer, total) => answer.toByteArray ++ total} ++
    authority.foldRight(Array[Byte]()){case(authority, total) => authority.toByteArray ++ total} ++
    additional.foldRight(Array[Byte]()){case(additional, total) => additional.toByteArray ++ total}
    
  def toCompressedByteArray(input: (Array[Byte], Map[String, Int])) = {
    val headerBytes = header.toCompressedByteArray(input)
    val queryBytes = query.foldRight(headerBytes) {case(question, total) => question.toCompressedByteArray(total)}
    (answers ++ authority ++ additional).foldLeft(queryBytes) {case(total, item) => item.toCompressedByteArray(total)}
  }
  
  private def domainName = query(0).qname.map(new String(_, "UTF-8")).mkString(".")
}

object Message {
  val logger = LoggerFactory.getLogger("app")
  
  def apply(buf: ByteBuf, offset: Int = 0) = {
    val header = Header(buf)
    new Message(
      header,
      deserialize(buf, header.questionCount, offset, deserializeQuestions),
      deserialize(buf, header.answerCount, offset, deserializeRRData),
      deserialize(buf, header.authorityCount, offset, deserializeRRData),
      deserialize(buf, header.additionalCount, offset, deserializeRRData)
    )
  }
  
  def deserialize[T: ClassTag](buf: ByteBuf, n: Int, o: Int, fn: (ByteBuf, Int, Int) => Array[T]) = 
    try {
      fn(buf, n, o)
    } catch {
      case e: Exception => {
        logger.debug(e.getStackTraceString)
        Array[T]()
      }
    }
  
  def deserializeQuestions(buf: ByteBuf, n: Int, o: Int): Array[Question] =
    if (n >= 1) Array.tabulate(n) { i => Question(buf, o) } else Array()

  def deserializeRRData(buf: ByteBuf, n: Int, o: Int): Array[RRData] = 
    if (n >= 1) Array.tabulate(n) { i => RRData(buf, o) }.filter(_.rdata != null) else Array()
}
