/*
 * Copyright 2009-2010 Roger Kapsi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ardverk.dht;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

import org.ardverk.concurrent.AsyncProcess;
import org.ardverk.io.IoUtils;
import org.ardverk.net.NetworkUtils;

import com.ardverk.dht.codec.DefaultMessageCodec;
import com.ardverk.dht.codec.MessageCodec;
import com.ardverk.dht.concurrent.ArdverkFuture;
import com.ardverk.dht.config.BootstrapConfig;
import com.ardverk.dht.config.Config;
import com.ardverk.dht.config.DefaultBootstrapConfig;
import com.ardverk.dht.config.DefaultGetConfig;
import com.ardverk.dht.config.DefaultLookupConfig;
import com.ardverk.dht.config.DefaultPingConfig;
import com.ardverk.dht.config.DefaultPutConfig;
import com.ardverk.dht.config.DefaultQuickenConfig;
import com.ardverk.dht.config.DefaultSyncConfig;
import com.ardverk.dht.config.GetConfig;
import com.ardverk.dht.config.LookupConfig;
import com.ardverk.dht.config.PingConfig;
import com.ardverk.dht.config.PutConfig;
import com.ardverk.dht.config.QuickenConfig;
import com.ardverk.dht.config.SyncConfig;
import com.ardverk.dht.entity.BootstrapEntity;
import com.ardverk.dht.entity.NodeEntity;
import com.ardverk.dht.entity.PingEntity;
import com.ardverk.dht.entity.QuickenEntity;
import com.ardverk.dht.entity.StoreEntity;
import com.ardverk.dht.entity.SyncEntity;
import com.ardverk.dht.entity.ValueEntity;
import com.ardverk.dht.io.transport.DatagramTransport;
import com.ardverk.dht.io.transport.Transport;
import com.ardverk.dht.message.DefaultMessageFactory;
import com.ardverk.dht.message.MessageFactory;
import com.ardverk.dht.routing.Contact;
import com.ardverk.dht.routing.DefaultRouteTable;
import com.ardverk.dht.routing.RouteTable;
import com.ardverk.dht.storage.Database;
import com.ardverk.dht.storage.DefaultDatabase;

public class SimpleArdverkDHT implements DHT, Closeable {

    public static final int DEFAULT_KEY_SIZE = 20;
    
    private volatile PingConfig pingConfig = new DefaultPingConfig();
    
    private volatile LookupConfig lookupConfig = new DefaultLookupConfig();
    
    private volatile GetConfig getConfig = new DefaultGetConfig();
    
    private volatile PutConfig putConfig = new DefaultPutConfig();
    
    private volatile BootstrapConfig bootstrapConfig = new DefaultBootstrapConfig();
    
    private volatile QuickenConfig quickenConfig = new DefaultQuickenConfig();
    
    private volatile SyncConfig syncConfig = new DefaultSyncConfig();
    
    private final ArdverkDHT dht;
    
    public SimpleArdverkDHT(int port) throws IOException {
        this(new DatagramTransport(port));
    }
    
    public SimpleArdverkDHT(String address, int port) throws IOException {
        this(new DatagramTransport(address, port));
    }
    
    public SimpleArdverkDHT(InetAddress address, int port) throws IOException {
        this(new DatagramTransport(address, port));
    }
    
    public SimpleArdverkDHT(SocketAddress address) throws IOException {
        this(new DatagramTransport(address));
    }
    
    public SimpleArdverkDHT(Transport transport) throws IOException {
        this(transport, null, null);
    }
    
    public SimpleArdverkDHT(Transport transport, 
            String secretKey, String initVector) throws IOException {
        Contact localhost = Contact.localhost(
                KUID.createRandom(DEFAULT_KEY_SIZE), 
                extract(transport.getSocketAddress()));
        
        MessageCodec codec = new DefaultMessageCodec(secretKey, initVector);
        
        MessageFactory messageFactory = new DefaultMessageFactory(
                DEFAULT_KEY_SIZE, localhost);
        
        Database database = new DefaultDatabase();
        RouteTable routeTable = new DefaultRouteTable(localhost);
        
        dht = new ArdverkDHT(codec, messageFactory, routeTable, database);
        dht.bind(transport);
    }
    
    public ArdverkDHT getArdverkDHT() {
        return dht;
    }

    @Override
    public void close() {
        IoUtils.close(dht);
    }
    
    @Override
    public <V> ArdverkFuture<V> submit(AsyncProcess<V> process, Config config) {
        return dht.submit(process, config);
    }

    @Override
    public <V> ArdverkFuture<V> submit(QueueKey queueKey,
            AsyncProcess<V> process, long timeout, TimeUnit unit) {
        return dht.submit(queueKey, process, timeout, unit);
    }

    @Override
    public Contact getLocalhost() {
        return dht.getLocalhost();
    }

    @Override
    public RouteTable getRouteTable() {
        return dht.getRouteTable();
    }

    @Override
    public Database getDatabase() {
        return dht.getDatabase();
    }
    
    @Override
    public void bind(Transport t) throws IOException {
        dht.bind(t);
    }

    @Override
    public void unbind() {
        dht.unbind();
    }

    @Override
    public boolean isBound() {
        return dht.isBound();
    }

    public ArdverkFuture<PingEntity> ping(String host, int port) {
        return ping(host, port, pingConfig);
    }

    public ArdverkFuture<PingEntity> ping(InetAddress address, int port) {
        return ping(address, port, pingConfig);
    }

    public ArdverkFuture<PingEntity> ping(SocketAddress address) {
        return ping(address, pingConfig);
    }
    
    public ArdverkFuture<PingEntity> ping(Contact dst) {
        return ping(dst, pingConfig);
    }
    
    @Override
    public ArdverkFuture<PingEntity> ping(String host, int port, PingConfig config) {
        return dht.ping(host, port, config);
    }

    @Override
    public ArdverkFuture<PingEntity> ping(InetAddress address, int port,
            PingConfig config) {
        return dht.ping(address, port, config);
    }

    @Override
    public ArdverkFuture<PingEntity> ping(SocketAddress address,
            PingConfig config) {
        return dht.ping(address, config);
    }

    @Override
    public ArdverkFuture<PingEntity> ping(Contact dst, PingConfig config) {
        return dht.ping(dst, config);
    }

    public ArdverkFuture<NodeEntity> lookup(KUID lookupId) {
        return lookup(lookupId, lookupConfig);
    }
    
    @Override
    public ArdverkFuture<NodeEntity> lookup(KUID lookupId, LookupConfig config) {
        return dht.lookup(lookupId, config);
    }

    public ArdverkFuture<ValueEntity> get(KUID key) {
        return get(key, getConfig);
    }
    
    @Override
    public ArdverkFuture<ValueEntity> get(KUID key, GetConfig config) {
        return dht.get(key, config);
    }

    public ArdverkFuture<StoreEntity> put(KUID key, byte[] value) {
        return put(key, value, putConfig);
    }
    
    @Override
    public ArdverkFuture<StoreEntity> put(KUID key, byte[] value,
            PutConfig config) {
        return dht.put(key, value, config);
    }

    public ArdverkFuture<StoreEntity> remove(KUID key) {
        return remove(key, putConfig);
    }
    
    @Override
    public ArdverkFuture<StoreEntity> remove(KUID key, PutConfig config) {
        return dht.remove(key, config);
    }
    
    public ArdverkFuture<BootstrapEntity> bootstrap(String host, int port) {
        return bootstrap(host, port, bootstrapConfig);
    }

    public ArdverkFuture<BootstrapEntity> bootstrap(InetAddress address,
            int port) {
        return bootstrap(address, port, bootstrapConfig);
    }

    public ArdverkFuture<BootstrapEntity> bootstrap(SocketAddress address) {
        return bootstrap(address, bootstrapConfig);
    }

    public ArdverkFuture<BootstrapEntity> bootstrap(Contact contact) {
        return dht.bootstrap(contact, bootstrapConfig);
    }
    
    @Override
    public ArdverkFuture<BootstrapEntity> bootstrap(String host, int port,
            BootstrapConfig config) {
        return dht.bootstrap(host, port, config);
    }

    @Override
    public ArdverkFuture<BootstrapEntity> bootstrap(InetAddress address,
            int port, BootstrapConfig config) {
        return dht.bootstrap(address, port, config);
    }

    @Override
    public ArdverkFuture<BootstrapEntity> bootstrap(SocketAddress address,
            BootstrapConfig config) {
        return dht.bootstrap(address, config);
    }

    @Override
    public ArdverkFuture<BootstrapEntity> bootstrap(Contact contact,
            BootstrapConfig config) {
        return dht.bootstrap(contact, config);
    }

    public ArdverkFuture<QuickenEntity> quicken() {
        return quicken(quickenConfig);
    }
    
    @Override
    public ArdverkFuture<QuickenEntity> quicken(QuickenConfig config) {
        return dht.quicken(config);
    }
    
    public ArdverkFuture<SyncEntity> sync() {
        return sync(syncConfig);
    }
    
    @Override
    public ArdverkFuture<SyncEntity> sync(SyncConfig config) {
        return dht.sync(config);
    }
    
    private static SocketAddress extract(SocketAddress address) {
        InetAddress addr = NetworkUtils.getAddress(address);
        if (!NetworkUtils.isPrivateAddress(addr)) {
            return address;
        }
        
        return new InetSocketAddress("localhost", NetworkUtils.getPort(address));
    }
}
