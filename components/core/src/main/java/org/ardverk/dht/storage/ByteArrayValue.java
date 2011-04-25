package org.ardverk.dht.storage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.ardverk.dht.KUID;
import org.ardverk.dht.codec.bencode.MessageInputStream;
import org.ardverk.dht.codec.bencode.MessageOutputStream;
import org.ardverk.dht.routing.Contact;
import org.ardverk.io.IoUtils;
import org.ardverk.utils.StringUtils;
import org.ardverk.version.VectorClock;

public class ByteArrayValue {

    public static final byte[] EMPTY = new byte[0];
    
    private final Contact creator;
    
    private final VectorClock<KUID> clock;
    
    private final byte[] value;
    
    public ByteArrayValue(Contact creator, 
            VectorClock<KUID> clock, byte[] value) {
        
        this.creator = creator;
        this.clock = clock;
        this.value = value;
    }
    
    public Contact getCreator() {
        return creator;
    }
    
    public VectorClock<KUID> getVectorClock() {
        return clock;
    }
    
    public byte[] getValue() {
        return value;
    }
    
    public int size() {
        return value.length;
    }
    
    public boolean isEmpty() {
        return size() == 0;
    }
    
    public ByteArrayValue update(Contact contact, byte[] value) {
        VectorClock<KUID> clock = this.clock;
        if (clock != null) {
            clock = clock.append(contact.getId());
        }
        
        return new ByteArrayValue(creator, clock, value);
    }
    
    @Override
    public String toString() {
        return StringUtils.toString(value);
    }
    
    public Resource toResource(ResourceId resourceId) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MessageOutputStream out = new MessageOutputStream(baos);
            out.writeContact(creator);
            out.writeVectorClock(clock);
            out.writeBytes(value);
            out.close();
            
            return new DefaultResource(resourceId, baos.toByteArray());
        } catch (IOException err) {
            throw new IllegalStateException("IOException", err);
        }
    }
    
    public static ByteArrayValue create(Resource resource) {
        MessageInputStream in = null;
        try {
            in = new MessageInputStream(resource.getContent());
            Contact creator = in.readContact();
            VectorClock<KUID> clock = in.readVectorClock();
            byte[] value = in.readBytes();
            return new ByteArrayValue(creator, clock, value);
        } catch (IOException err) {
            throw new IllegalStateException("IOException", err);
        } finally {
            IoUtils.close(in);
        }
    }
}
