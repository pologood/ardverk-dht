package com.ardverk.dht.message;

import java.util.ServiceLoader;

public abstract class MessageCodec extends MessageCodecSpi {

    private static final ServiceLoader<MessageCodec> codecs 
        = ServiceLoader.load(MessageCodec.class);
    
    public static MessageCodec getCodec(String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        
        for (MessageCodec codec : codecs) {
            if (name.equals(codec.getName())) {
                return codec;
            }
        }
        
        throw new IllegalArgumentException("name=" + name);
    }

    protected MessageCodec(String name) {
        super(name);
    }
}