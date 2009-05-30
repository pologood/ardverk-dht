package com.ardverk.dht.message;

import java.io.IOException;
import java.net.SocketAddress;

public abstract class MessageCodecSpi {

    private final String name;
    
    protected MessageCodecSpi(String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public abstract byte[] serialize(Message message, SocketAddress dst) throws IOException;
    
    public abstract Message deserialize(SocketAddress src, byte[] data) throws IOException;
    
    @Override
    public String toString() {
        return name;
    }
}
