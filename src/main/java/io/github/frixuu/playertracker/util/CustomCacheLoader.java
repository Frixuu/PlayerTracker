package io.github.frixuu.playertracker.util;

import com.google.common.cache.CacheLoader;

@FunctionalInterface
public interface CustomCacheLoader<K, V> {

    V loadValue(K key) throws Exception;

    default CacheLoader<K, V> into() {
        return new CacheLoader<>() {
            @Override
            public V load(K key) throws Exception {
                return loadValue(key);
            }
        };
    }
}
