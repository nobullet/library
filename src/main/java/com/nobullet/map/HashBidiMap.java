package com.nobullet.map;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

interface BidiMap<K, V> extends Map<K, V> {

    K getByValue(V value);

    K removeByValue(V value);
}

/**
 * Hash bidi map. Not thread-safe.
 *
 * @param <K> Key type.
 * @param <V> Value type.
 */
public class HashBidiMap<K, V> implements BidiMap<K, V> {

    Map<K, V> kvMap;
    Map<V, K> vkMap;

    public HashBidiMap() {
        kvMap = new HashMap<>();
        vkMap = new HashMap<>();
    }

    @Override
    public K getByValue(V value) {
        return vkMap.get(value);
    }

    @Override
    public V put(K key, V value) {
        V existingValue = kvMap.get(key);
        if (existingValue != null) {
            kvMap.remove(key);
            vkMap.remove(existingValue);
        }
        K existingKey = vkMap.get(value);
        if (existingKey != null) {
            kvMap.remove(existingKey);
            vkMap.remove(value);
        }

        kvMap.put(key, value);
        vkMap.put(value, key);
        return existingValue;
    }

    @Override
    public K removeByValue(V value) {
        K toRemove = vkMap.get(value);
        if (toRemove != null) {
            vkMap.remove(value);
            kvMap.remove(toRemove);
        }
        return toRemove;
    }

    @Override
    public V remove(Object key) {
        V toRemove = kvMap.get((K) key);
        if (toRemove != null) {
            kvMap.remove(key);
            vkMap.remove(toRemove);
        }
        return toRemove;
    }

    @Override
    public int size() {
        return kvMap.size();
    }

    @Override
    public boolean isEmpty() {
        return kvMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return kvMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return vkMap.containsKey(value);
    }

    @Override
    public V get(Object key) {
        return kvMap.get(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        kvMap.clear();
        vkMap.clear();
    }

    @Override
    public Set<K> keySet() {
        return kvMap.keySet();
    }

    @Override
    public Collection<V> values() {
        return kvMap.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return kvMap.entrySet();
    }
}
