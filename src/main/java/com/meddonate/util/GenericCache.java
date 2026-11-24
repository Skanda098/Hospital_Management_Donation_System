package com.meddonate.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GenericCache<K, V> {
    private final Map<K, V> store = new HashMap<>();

    public void put(K key, V value) {
        store.put(key, value);
    }

    public V get(K key) {
        return store.get(key);
    }

    public void remove(K key) {
        store.remove(key);
    }

    public Collection<V> getAll() {
        return store.values();
    }

    public void clear() {
        store.clear();
    }

    public int size() {
        return store.size();
    }
}