package dsa;

import java.util.HashMap;
import java.util.Map;

public class HashMapManager<K, V> {
    private final Map<K, V> map = new HashMap<>();

    public void put(K key, V value) {
        map.put(key, value);
    }

    public V get(K key) {
        return map.get(key);
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    public V remove(K key) {
        return map.remove(key);
    }

    public Map<K, V> asMap() {
        return map;
    }
}
