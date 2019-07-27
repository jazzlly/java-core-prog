package com.ryan.java.demo.collection.map;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

@Slf4j
public class HashMapDemo {

    /**
     * HashMap关键概念：
     *  初始容量：初始的桶的数量， 默认为16
     *  负载因子：默认为0.75
     *  当hashmap中的成员数量 大于 负载因子*当前容量时，
     *      hashmap将会被重新hash，使得桶的数量翻倍
     *
     *  如果能过估计key的数量，可以根据key的数量来设置初始容量
     *      尽可能不用重新hash
     *
     */
    @Test
    public void hashMap_nullKey() {
        HashMap<String, String> hashMap = new HashMap<>();

        // null key, non-null value
        assertThat(hashMap.get(null)).isNull();

        // null key, non-null value
        hashMap.put(null, "foo");
        assertThat(hashMap.get(null)).isEqualTo("foo");

        hashMap.remove(null);
        assertThat(hashMap.get(null)).isNull();

        // non-null key, null value
        assertThat(hashMap.get("foo")).isNull();
        hashMap.put("foo", null);
        assertThat(hashMap.get("foo")).isNull();
    }

    @Test
    public void treeMap_order() {
        Random random = new Random();
        TreeMap<Integer, String> treeMap = new TreeMap<>();
        for (int i = 0; i < 10; i++) {
            treeMap.put(random.nextInt(100), UUID.randomUUID().toString());
        }

        // natual order
        for (Map.Entry<Integer, String> entry : treeMap.entrySet()) {
            log.info(entry.getKey() + ": " + entry.getValue());
        }
    }

    @Test
    public void linkedHashMap_as_insertOrder() {
        LinkedHashMap<String, Integer> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("语文", 1);
        linkedHashMap.put("数学", 2);
        linkedHashMap.put("英语", 3);
        linkedHashMap.put("历史", 4);
        linkedHashMap.put("政治", 5);
        linkedHashMap.put("地理", 6);
        linkedHashMap.put("生物", 7);
        linkedHashMap.put("化学", 8);

        // LinkedHashMap has insertion-order by default
        // LinkedHashMap的迭代输出的结果保持了插入顺序
        assertThat(linkedHashMap).containsExactly(
                entry("语文", 1),
                entry("数学", 2),
                entry("英语", 3),
                entry("历史", 4),
                entry("政治", 5),
                entry("地理", 6),
                entry("生物", 7),
                entry("化学", 8));
    }

    @Test
    public void linkedHashMap_as_LRU() {
        // need accessOrder be true for LRU
        LinkedHashMap<String, String> linkedHashMap =
                new LinkedHashMap<String, String>(16, 0.75F, true) {
                    @Override
                    protected boolean removeEldestEntry(Map.Entry eldest) {
                        return size() > 3;
                    }
                };
        linkedHashMap.put("foo", "1");
        linkedHashMap.put("bar", "2");
        linkedHashMap.put("wah", "3");

        // System.out.println(linkedHashMap);
        assertThat(linkedHashMap).containsExactly(
                entry("foo", "1"),
                entry("bar", "2"),
                entry("wah", "3"));

        // not access bar, bar will be deleted
        assertThat(linkedHashMap.get("wah")).isNotNull();
        assertThat(linkedHashMap.get("wah")).isNotNull();
        assertThat(linkedHashMap.get("foo")).isNotNull();

        // delect trigger
        linkedHashMap.put("zar", "4");
        System.out.println(linkedHashMap);

        // bar is removed
        assertThat(linkedHashMap.keySet()).doesNotContain("bar");
    }
    /**
     * LinkedList的要点：
     *  通过下面三个回调函数维护了链表
     *
     * // Callbacks to allow LinkedHashMap post-actions
     * void afterNodeAccess(Node<K,V> p) { }
     * void afterNodeInsertion(boolean evict) { }
     * void afterNodeRemoval(Node<K,V> p) { }
     *
     * 在accessOrder模式下，只要执行get或者put等操作的时候，就会产生structural modification。
     *  不要犯了像ConcurrentModificationException with LinkedHashMap类似的问题
     */
}
