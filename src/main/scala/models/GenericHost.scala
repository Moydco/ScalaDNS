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

package models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import utils.HostnameUtils

@JsonIgnoreProperties(Array("typ"))
case class GenericHost(
  @JsonProperty("type") override val typ: String = null,
  @JsonProperty("class") cls: String = null,
  @JsonProperty("name") name: String = null,
  @JsonProperty("value") value: String = "",
  @JsonProperty("ttl") timetolive: Long  
) extends Host(typ) {
  def setName(newname: String) = GenericHost(typ, cls, newname, value, timetolive)
  
  override def toAbsoluteNames(domain: ExtendedDomain) = 
    new GenericHost(typ, cls, HostnameUtils.absoluteHostName(name, domain.fullName), value, timetolive)
  
  override def equals(other: Any) = other match {
    case h: GenericHost => h.cls == cls && h.name == name && h.value == value
    case _ => false
  }
  
  protected def getRData = Unit
}