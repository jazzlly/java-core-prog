package com.ryan.java.demo.concurrent;

import org.junit.Test;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * SkipListMap is sorted!
 */
public class C71SkipList {

    @Test
    public void smoke() {
        ConcurrentSkipListMap<Integer, String> listMap =
                new ConcurrentSkipListMap<>();

        for (int i = 0; i < 10; i++) {
            listMap.put(i, UUID.randomUUID().toString());
        }

        for (Map.Entry<Integer, String> entry : listMap.entrySet()) {
            System.out.println("key: " + entry.getKey() +
                    ", value: " + entry.getValue());
        }
    }
}
