/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avro.specific;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/** Base class for generated record classes. */
public abstract class SpecificRecordBase
    implements SpecificRecord, Comparable<SpecificRecord>, GenericRecord, Externalizable {

  @Override
  public abstract Schema getSchema();

  @Override
  public abstract Object get(int field);

  @Override
  public abstract void put(int field, Object value);

  public SpecificData getSpecificData() {
    // Default implementation for backwards compatibility, overridden in generated
    // code
    return SpecificData.get();
  }

  @Override
  public void put(String fieldName, Object value) {
    Schema.Field field = getSchema().getField(fieldName);
    if (field == null) {
      throw new AvroRuntimeException("Not a valid schema field: " + fieldName);
    }
    put(field.pos(), value);
  }

  @Override
  public Object get(String fieldName) {
    Schema.Field field = getSchema().getField(fieldName);
    if (field == null) {
      throw new AvroRuntimeException("Not a valid schema field: " + fieldName);
    }
    return get(field.pos());
  }

  @Override
  public boolean equals(Object that) {
    if (that == this)
      return true; // identical object
    if (!(that instanceof SpecificRecord))
      return false; // not a record
    if (this.getClass() != that.getClass())
      return false; // not same schema
    return getSpecificData().compare(this, that, this.getSchema(), true) == 0;
  }

  @Override
  public int hashCode() {
    return getSpecificData().hashCode(this, this.getSchema());
  }

  @Override
  public int compareTo(SpecificRecord that) {
    return getSpecificData().compare(this, that, this.getSchema());
  }

  @Override
  public String toString() {
    return getSpecificData().toString(this);
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    new SpecificDatumWriter(getSchema()).write(this, SpecificData.getEncoder(out));
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException {
    new SpecificDatumReader(getSchema()).read(this, SpecificData.getDecoder(in));
  }
}
