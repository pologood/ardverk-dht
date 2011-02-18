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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.ardverk.concurrent.AsyncFuture;
import org.ardverk.concurrent.AsyncFutureListener;
import org.ardverk.concurrent.CountDown;
import org.ardverk.concurrent.FutureUtils;
import org.ardverk.dht.concurrent.DHTFuture;
import org.ardverk.dht.concurrent.DHTValueFuture;
import org.ardverk.dht.config.LookupConfig;
import org.ardverk.dht.config.PingConfig;
import org.ardverk.dht.config.QuickenConfig;
import org.ardverk.dht.entity.DefaultQuickenEntity;
import org.ardverk.dht.entity.NodeEntity;
import org.ardverk.dht.entity.PingEntity;
import org.ardverk.dht.entity.QuickenEntity;
import org.ardverk.dht.routing.Bucket;
import org.ardverk.dht.routing.Contact;
import org.ardverk.dht.routing.RouteTable;
import org.ardverk.dht.utils.IdentifierUtils;
import org.ardverk.lang.TimeStamp;


/**
 * The {@link QuickenManager} provides methods to keep 
 * the {@link RouteTable} fresh.
 */
public class QuickenManager {

    private final DHT dht;
    
    private final RouteTable routeTable;
    
    QuickenManager(DHT dht, RouteTable routeTable) {
        this.dht = dht;
        this.routeTable = routeTable;
    }
    
    public DHTFuture<QuickenEntity> quicken(QuickenConfig config) {
        
        TimeStamp creationTime = TimeStamp.now();
        
        List<DHTFuture<PingEntity>> pingFutures 
            = new ArrayList<DHTFuture<PingEntity>>();
        
        List<DHTFuture<NodeEntity>> lookupFutures 
            = new ArrayList<DHTFuture<NodeEntity>>();
        
        synchronized (routeTable) {
            int pingCount = (int)(routeTable.getK() * config.getPingCount());
            
            Contact localhost = routeTable.getLocalhost();
            KUID localhostId = localhost.getId();

            if (0 < pingCount) {
                PingConfig pingConfig = config.getPingConfig();
                long contactTimeout = config.getContactTimeoutInMillis();
                
                Contact[] contacts = routeTable.select(localhostId, pingCount);
                for (Contact contact : contacts) {
                    // Don't send PINGs to the localhost!
                    if (contact.equals(localhost)) {
                        continue;
                    }
                    
                    if (contact.isTimeout(contactTimeout, TimeUnit.MILLISECONDS)) {
                        DHTFuture<PingEntity> future 
                            = dht.ping(contact, pingConfig);
                        pingFutures.add(future);
                    }
                }
            }
            
            LookupConfig lookupConfig = config.getLookupConfig();
            long bucketTimeout = config.getBucketTimeoutInMillis();
            
            Bucket[] buckets = routeTable.getBuckets();
            IdentifierUtils.byXor(buckets, localhostId);
            
            for (Bucket bucket : buckets) {
                if (bucket.contains(localhostId)) {
                    continue;
                }
                
                TimeStamp timeStamp = bucket.getTimeStamp();
                if (timeStamp.getAgeInMillis() < bucketTimeout) {
                    continue;
                }
                
                // Select a random ID with this prefix
                KUID randomId = KUID.createWithPrefix(
                        bucket.getId(), bucket.getDepth());
                
                DHTFuture<NodeEntity> future 
                    = dht.lookup(randomId, lookupConfig);
                lookupFutures.add(future);
            }
        }
        
        @SuppressWarnings("unchecked")
        DHTFuture<PingEntity>[] pings 
            = pingFutures.toArray(new DHTFuture[0]);
        
        @SuppressWarnings("unchecked")
        DHTFuture<NodeEntity>[] lookups 
            = lookupFutures.toArray(new DHTFuture[0]);
        
        return new QuickenFuture(creationTime, pings, lookups);
    }
    
    public static class QuickenFuture extends DHTValueFuture<QuickenEntity> {
        
        private final CountDown countDown;
        
        private final TimeStamp timeStamp;
        
        private final DHTFuture<PingEntity>[] pingFutures;
        
        private final DHTFuture<NodeEntity>[] lookupFutures;
        
        @SuppressWarnings("unchecked")
        private QuickenFuture(TimeStamp timeStamp, 
                DHTFuture<PingEntity>[] pingFutures, 
                DHTFuture<NodeEntity>[] lookupFutures) {
            this.timeStamp = timeStamp;
            this.pingFutures = pingFutures;
            this.lookupFutures = lookupFutures;
            
            countDown = new CountDown(pingFutures.length + lookupFutures.length);
            
            // It's possible that countdown is 0!
            if (0 < countDown.get()) {
                AsyncFutureListener<?> listener 
                        = new AsyncFutureListener<Object>() {
                    @Override
                    public void operationComplete(AsyncFuture<Object> future) {
                        coutdown();
                    }
                };
                
                for (DHTFuture<PingEntity> future : pingFutures) {
                    future.addAsyncFutureListener(
                            (AsyncFutureListener<PingEntity>)listener);
                }
                
                for (DHTFuture<NodeEntity> future : lookupFutures) {
                    future.addAsyncFutureListener(
                            (AsyncFutureListener<NodeEntity>)listener);
                }
            } else {
                complete();
            }
        }
        
        public DHTFuture<PingEntity>[] getPingFutures() {
            return pingFutures;
        }

        public DHTFuture<NodeEntity>[] getLookupFutures() {
            return lookupFutures;
        }
        
        @Override
        protected void done() {
            super.done();
            
            FutureUtils.cancelAll(pingFutures, true);
            FutureUtils.cancelAll(lookupFutures, true);
        }
        
        private void coutdown() {
            if (countDown.countDown()) {
                complete();
            }
        }
        
        private void complete() {
            long time = timeStamp.getAgeInMillis();
            setValue(new DefaultQuickenEntity(pingFutures, lookupFutures, 
                    time, TimeUnit.MILLISECONDS));
        }
    }
}