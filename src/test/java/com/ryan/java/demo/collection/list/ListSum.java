package com.ryan.java.demo.collection.list;

public class ListSum {
    /**
     * 无论哪种实现，按值返回下标contains（e）, indexOf（e）, remove（e）
     * 都需遍历所有元素进行比较，性能可想像的不会太好。
     *
     * 没有按元素值排序的SortedList。
     *
     * 除了CopyOnWriteArrayList，再没有其他线程安全又并发优化的实现如ConcurrentLinkedList。
     * 凑合着用Set与Queue中的等价类时，会缺少一些List特有的方法如get（i）。
     * 如果更新频率较高，或数组较大时，还是得用Collections.synchronizedList（list），
     * 对所有操作用同一把锁来保证线程安全。
     */
}
