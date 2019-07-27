package com.ryan.java.demo.concurrent.collection;

import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapDemo {

    /**
     * ConcurrentHashMap
     *  使用分离锁来实现
     *
     *  提供了不会发送ConcurrentModification的迭代器
     *      不需要在迭代容器时加锁？
     *
     *  已经提供了
     *      put-if-absent
     *      remove-if-equal
     *      replice-if-equal
     *
     * 由于没有使用独占锁，所以不能像Collections.synchronizedMap
     *      那样对客户端的map进行加锁
     */
    @Test
    public void smoke() {
        ConcurrentHashMap<String, String> hashMap = new ConcurrentHashMap<>();

        // 新的同步操作
        hashMap.putIfAbsent("foo", "foo");
        hashMap.remove("foo", "foo");
        hashMap.replace("foo", "bar", "kar");
    }
}
