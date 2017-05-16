package com.phonecompany.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class SimpleCacheImpl<K, V> {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleCacheImpl.class);
    private final ConcurrentMap<K, Future<V>> cache = new ConcurrentHashMap<>();

    public SimpleCacheImpl(long expirationTime) {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::clear,
                expirationTime, expirationTime, TimeUnit.SECONDS);
    }

    public void put(K key, V value) {
        createIfAbsent(key, () -> value);
    }

    private void createIfAbsent(final K key, final Callable<V> callable) {
        Future<V> future = cache.get(key);
        if (future == null) {
            final FutureTask<V> futureTask = new FutureTask<>(callable);
            future = cache.putIfAbsent(key, futureTask);
            if (future == null) {
                // Compute the value
                futureTask.run();
            }
        }
    }

    public V getValue(final K key)
            throws InterruptedException, ExecutionException {
        Future<V> future = cache.getOrDefault(key, null);
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Clean-up the cache entries.
     */
    public void clear() {
        // Clear the cache
        cache.clear();
    }

    public int getSize() {
        return cache.size();
    }

    public boolean contains(K key) {
        return cache.containsKey(key);
    }

    public void remove(K key) {
        cache.remove(key);
    }
}
