package com.andorid.java.javasolution;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

import com.andorid.java.model.Employee;

public class LFUCache {
    private static final int MAX_SIZE = 100000;
    private final int LAST_ACCESS_IN_MILLISECS = 5000;

    HashMap<Integer, Employee> vals;    //data to store
    HashMap<Integer, Integer> counts;   //how many times was a key accessed
    HashMap<Integer, LinkedHashSet<Integer>> lists; //list to store the hit count for each key

    int cap;
    int min = -1;

    public LFUCache(int capacity) {
        cap = capacity;
        vals = new HashMap<>();
        counts = new HashMap<>();
        lists = new HashMap<>();
        lists.put(1, new LinkedHashSet<>());
    }

    public Employee get(int key) {
        if (!vals.containsKey(key))
            return null;

        // Get the count from counts map
        int count = counts.get(key);

        // increase the counter
        counts.put(key, count + 1);

        // remove the element from the counter to linkedhashset
        lists.get(count).remove(key);

        // when current min does not have any data, next one would be the min
        if (count == min && lists.get(count).size() == 0)
            min++;
        if (!lists.containsKey(count + 1))
            lists.put(count + 1, new LinkedHashSet<>());

        lists.get(count + 1).add(key);

        return vals.get(key);
    }

    public void set(int key, Employee value) {
        if(vals.size() > MAX_SIZE)
            removeOldCacheEntries();

        if (cap <= 0)
            return;

        // If key does exist, we are returning from here
        if (vals.containsKey(key)) {
            vals.put(key, value);
            get(key);
            return;
        }

        if (vals.size() >= cap) {
            int evit = lists.get(min).iterator().next();
            System.out.println("The item in index " + evit + " is removed - " + vals.get(evit).getName());
            lists.get(min).remove(evit);
            vals.remove(evit);
            counts.remove(evit);
        }

        // If the key is new, insert the value and current min should be 1 of course
        vals.put(key, value);
        counts.put(key, 1);
        min = 1;
        lists.get(1).add(key);
    }

    private void removeOldCacheEntries() {
        long currentAgeInSecs = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        ArrayList<Integer> keysToRemove = new ArrayList<>();
        for (int i = 0; i < vals.size(); i++) {
            if (Math.abs(vals.get(i).getAgeInSecs() - currentAgeInSecs) > LAST_ACCESS_IN_MILLISECS) {
                keysToRemove.add(i);
            }
        }
        for (int i : keysToRemove) {
            vals.remove(i);
            counts.remove(i);
            lists.get(min).clear();
        }
    }

    public static void main(String[] args) {
        LFUCache cache = new LFUCache(MAX_SIZE);
        try {
            for (int i = 0; i < 11000; i++) {
                cache.set(i, new Employee("John_" + i));
            }
        } catch (Exception e) {
            System.out.println("baj van");
        }
        System.out.println(cache.vals.size());
    }

}
