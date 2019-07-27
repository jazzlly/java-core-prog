package com.ryan.java.demo.collection.map;

import org.junit.Test;

import java.util.ConcurrentModificationException;
import java.util.LinkedHashMap;
import java.util.Map;

public class LinkedHashMapException {

    private static final int MAX_SIZE = 3;

    private LinkedHashMap<String,Integer> lru_cache =
            new LinkedHashMap<String,Integer>(MAX_SIZE, 0.75F, true){
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
            return lru_cache.size() > MAX_SIZE;
        }
    };

    @Test(expected = ConcurrentModificationException.class)
    public void smoke() {
        lru_cache.put("Di", 1);
        lru_cache.put("Da", 1);
        lru_cache.put("Doo", 1);
        lru_cache.put("Sa", 2);

        // 当accessOrder为true时，LinkedHashMap::get方法也会修改其内部的结构，
        // 所以不能再iterable中使用
        for(String key : lru_cache.keySet()){
            System.out.println(lru_cache.get(key));
        }
    }
}

