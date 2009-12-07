package com.ardverk.dht.message;

import java.net.InetAddress;

import com.ardverk.dht.routing.Contact;

public abstract class AbstractRequestMessage extends AbstractMessage 
        implements RequestMessage {

    public AbstractRequestMessage(
            MessageId messageId, Contact contact, 
            long time, InetAddress address) {
        super(messageId, contact, time, address);
    }
}
