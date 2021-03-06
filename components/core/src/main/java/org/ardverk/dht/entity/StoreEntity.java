/*
 * Copyright 2009-2012 Roger Kapsi
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

package org.ardverk.dht.entity;

import java.util.concurrent.TimeUnit;

import org.ardverk.dht.message.StoreResponse;
import org.ardverk.dht.routing.Contact;
import org.ardverk.dht.rsrc.Key;
import org.ardverk.dht.rsrc.Value;

/**
 * A default implementation of {@link StoreEntity}.
 */
public class StoreEntity extends Entity {

  private final Contact[] contacts;
  
  private final Key key;
  
  private final Value value;
  
  private final StoreResponse[] responses;
  
  public StoreEntity(Contact[] contacts, Key key, 
      Value value, StoreResponse[] responses, 
      long time, TimeUnit unit) {
    super(time, unit);
    
    this.contacts = contacts;
    this.key = key;
    this.value = value;
    this.responses = responses;
  }
  
  public Contact[] getContacts() {
    return contacts;
  }

  public Key getKey() {
    return key;
  }

  public Value getValue() {
    return value;
  }
  
  public Contact[] getStoreContacts() {
    Contact[] contacts = new Contact[responses.length];
    for (int i = 0; i < responses.length; i++) {
      contacts[i] = responses[i].getContact();
    }
    return contacts;
  }

  public StoreResponse[] getStoreResponses() {
    return responses;
  }
}