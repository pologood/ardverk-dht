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

package org.ardverk.dht.io.transport;

import java.io.IOException;
import java.net.SocketAddress;

import org.ardverk.lang.Bindable;

/**
 * The {@link Transport} provides a generic interface for the DHT to 
 * send and receive messages over UDP or any other transport layer.
 */
public interface Transport extends Endpoint, Bindable<TransportCallback> {

  @Override
  public void bind(TransportCallback callback) throws IOException;
  
  @Override
  public void unbind() throws IOException;
  
  /**
   * Returns the local {@link SocketAddress}
   */
  public SocketAddress getSocketAddress();
}