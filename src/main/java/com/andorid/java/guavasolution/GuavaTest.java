package com.andorid.java.guavasolution;

import com.andorid.java.model.Employee;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;

public class GuavaTest {
    private final int MAX_SIZE = 100000;
    public LoadingCache<String, Employee> cache;

    public GuavaTest(int concurrency) {
        CacheLoader<String, Employee> loader;
        loader = new CacheLoader<>() {
            @Override
            public Employee load(String key) {
                return new Employee(key);
            }
        };

        RemovalListener<String, Employee> listener;
        listener = n -> {
            if (n.wasEvicted()) {
                System.out.println("Removed: " + n.getValue().getName());    //Just add to log of removed entry
            }
        };

        cache = CacheBuilder.newBuilder()
            .maximumSize(MAX_SIZE)  //max size, Eviction policy
            .removalListener(listener)  //Removal listener
            .concurrencyLevel(concurrency) //Support concurrency
            .recordStats() //Give statistic to user
            .build(loader);
    }

    public static void main(String[] args) {
        GuavaTest gt = new GuavaTest(50);
        gt.whenEntryRemovedFromCache_thenNotify();
    }

    private void whenEntryRemovedFromCache_thenNotify() {
        for (int i = 0; i < MAX_SIZE + 1; i++) {
            cache.getUnchecked("Tom_" + i);
        }
        System.out.println("Size of cache: " + cache.size());

        System.out.println(cache.stats());
    }

}
