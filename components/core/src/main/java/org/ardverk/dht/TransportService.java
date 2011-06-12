/*
 * Copyright 2009-2011 Roger Kapsi
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

package org.ardverk.dht;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;

import org.ardverk.dht.io.transport.Transport;
import org.ardverk.lang.Bindable;

/**
 * The {@link TransportService} binds the {@link DHT} 
 * to a {@link Transport} layer.
 */
interface TransportService extends Bindable<Transport> {

    /**
     * Binds the {@link DHT} to the given port.
     * 
     * @see #bind(Transport)
     * @see #bind(SocketAddress)
     */
    public void bind(int port) throws IOException;
    
    /**
     * Binds the {@link DHT} to the given host-port.
     * 
     * @see #bind(Transport)
     * @see #bind(SocketAddress)
     */
    public void bind(String host, int port) throws IOException;
    
    /**
     * Binds the {@link DHT} to the given {@link InetAddress} and port.
     * 
     * @see #bind(Transport)
     * @see #bind(SocketAddress)
     */
    public void bind(InetAddress bindaddr, int port) throws IOException;
    
    /**
     * Binds the {@link DHT} to the given {@link SocketAddress}.
     * 
     * @see #bind(Transport)
     */
    public void bind(SocketAddress address) throws IOException;
    
    @Override
    public void bind(Transport transport) throws IOException;
    
    @Override
    public void unbind() throws IOException;
}