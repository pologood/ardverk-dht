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

package com.ardverk.dht.easy;

import com.ardverk.dht.config.BootstrapConfig;
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

public class EasyConfig {
    
    private static final int DEFAULT_KEY_SIZE = 20; // SHA-1
    
    private final int keySize;
    
    private volatile PingConfig pingConfig = new DefaultPingConfig();
    
    private volatile LookupConfig lookupConfig = new DefaultLookupConfig();
    
    private volatile GetConfig getConfig = new DefaultGetConfig();
    
    private volatile PutConfig putConfig = new DefaultPutConfig();
    
    private volatile BootstrapConfig bootstrapConfig = new DefaultBootstrapConfig();
    
    private volatile QuickenConfig quickenConfig = new DefaultQuickenConfig();
    
    private volatile SyncConfig syncConfig = new DefaultSyncConfig();
    
    private volatile String secretKey = null;
    
    private volatile String initVector = null;
    
    public EasyConfig() {
        this(DEFAULT_KEY_SIZE);
    }
    
    public EasyConfig(int keySize) {
        this.keySize = keySize;
    }

    public int getKeySize() {
        return keySize;
    }
    
    public PingConfig getPingConfig() {
        return pingConfig;
    }

    public void setPingConfig(PingConfig pingConfig) {
        this.pingConfig = pingConfig;
    }

    public LookupConfig getLookupConfig() {
        return lookupConfig;
    }

    public void setLookupConfig(LookupConfig lookupConfig) {
        this.lookupConfig = lookupConfig;
    }

    public GetConfig getGetConfig() {
        return getConfig;
    }

    public void setGetConfig(GetConfig getConfig) {
        this.getConfig = getConfig;
    }

    public PutConfig getPutConfig() {
        return putConfig;
    }

    public void setPutConfig(PutConfig putConfig) {
        this.putConfig = putConfig;
    }

    public BootstrapConfig getBootstrapConfig() {
        return bootstrapConfig;
    }

    public void setBootstrapConfig(BootstrapConfig bootstrapConfig) {
        this.bootstrapConfig = bootstrapConfig;
    }

    public QuickenConfig getQuickenConfig() {
        return quickenConfig;
    }

    public void setQuickenConfig(QuickenConfig quickenConfig) {
        this.quickenConfig = quickenConfig;
    }

    public SyncConfig getSyncConfig() {
        return syncConfig;
    }

    public void setSyncConfig(SyncConfig syncConfig) {
        this.syncConfig = syncConfig;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getInitVector() {
        return initVector;
    }

    public void setInitVector(String initVector) {
        this.initVector = initVector;
    }
}