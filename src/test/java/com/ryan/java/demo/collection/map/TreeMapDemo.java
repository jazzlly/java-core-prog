package com.ryan.java.demo.collection.map;

import org.junit.Test;

import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * TreeMap是有序的， natural order
 * TreeSet内部实现就是一个TreeMap, Key是Set的对象，value是一个dummy对象
 *
 * 有序的Map还包括ConcurrentSkipListMap
 */
public class TreeMapDemo {

    @Test
    public void smoke() {
        TreeMap<String, String> treeMap = new TreeMap<>();
        treeMap.put("foo", "bar");
    }
}
