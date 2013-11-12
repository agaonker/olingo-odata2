/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 ******************************************************************************/
package org.apache.olingo.odata2.ref.annotation.model;

import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.edm.EdmSimpleTypeKind;

/**
 *
 */
@EdmEntityType(name="Base", namespace=ModelSharedConstants.NAMESPACE_1)
public abstract class RefBase {
  @EdmProperty(name="Name")
  protected String name;
  @EdmProperty(name="Id", type = EdmSimpleTypeKind.String)
  @EdmKey
  protected String id;

  public String getName() {
    return name;
  }

  public String getId() {
    return id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setId(int id) {
    this.id = String.valueOf(id);
  }

  @Override
  public int hashCode() {
    if(id == null) {
      return -1;
    }
    return id.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final RefBase other = (RefBase) obj;
    if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
      return false;
    }
    if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
      return false;
    }
    return true;
  }
}
