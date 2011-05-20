package org.ardverk.dht.storage;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.ardverk.collection.CollectionUtils;
import org.ardverk.dht.rsrc.Value;
import org.ardverk.version.Occured;
import org.ardverk.version.VectorClock;

public class VectorClockMap<K, V extends Value> {
    
    private final Map<VectorClock<K>, V> map 
        = new LinkedHashMap<VectorClock<K>, V>();
    
    public void upsert(VectorClock<K> key, V value) {
        
        Iterator<VectorClock<K>> it = map.keySet().iterator();
        while (it.hasNext()) {
            VectorClock<K> existing = it.next();
            
            Occured occured = VectorClockUtils.compare(existing, key);
            switch (occured) {
                case AFTER:
                    it.remove();
                    break;
            }
        }
        
        map.put(key, value);
    }
    
    public boolean remove(VectorClock<K> key) {
        return map.remove(key) != null;
    }
    
    public V value() {
        return CollectionUtils.last(map.values());
    }
    
    public Value[] values() {
        return map.values().toArray(new Value[0]);
    }
    
    public int size() {
        return map.size();
    }
    
    public boolean isEmpty() {
        return map.isEmpty();
    }
}
