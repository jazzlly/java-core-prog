package com.ryan.java.demo.collection.list;

import org.junit.Test;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public class CopyOnWriteArrayListDemo {
    /**
     * 并发优化的ArrayList。基于不可变对象策略，在修改时先复制出一个数组快照来修改，
     * 改好了，再让内部指针指向新数组。
     *
     * 因为对快照的修改对读操作来说不可见，所以读读之间不互斥，读写之间也不互斥，
     * 只有写写之间要加锁互斥。但复制快照的成本昂贵，典型的适合读多写少的场景。
     *
     * 虽然增加了addIfAbsent（e）方法，会遍历数组来检查元素是否已存在，性能可想像的不会太好。
     *
     */

    @Test
    public void smoke() {
        CopyOnWriteArraySet<String> strings = new CopyOnWriteArraySet<>();
        CopyOnWriteArrayList<String> strings1 = new CopyOnWriteArrayList<>();
    }
}
