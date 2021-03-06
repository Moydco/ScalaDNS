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

package utils
 
import java.util.Calendar
import scala.collection.mutable.StringBuilder
/*
    Serial Format: YYYYMMDDII where II is an integer between 0 and 9
 */


object SerialParser {


  def serialHasChanged(originalSerial: String, newSerial: String):Boolean = {
    val oSerial = originalSerial.toLong
    val nSerial = newSerial.toLong
    nSerial > oSerial
  }

  def updateSerial(originalSerial: String): Long = {
    originalSerial.toLong + 1
  }

  def generateNewSerial():Long = {

    val year = Calendar.getInstance().get(Calendar.YEAR)
    val month = Calendar.getInstance().get(Calendar.MONTH)
    val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    val serial = new StringBuilder().append(year.toString).append(month.toString).append(day.toString).append("00").toString
    serial.toLong
  }


}
