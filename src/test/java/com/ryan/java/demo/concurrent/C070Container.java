package com.ryan.java.demo.concurrent;

import net.jcip.annotations.GuardedBy;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.*;

public class C070Container {

    /**
     * 同步容器是线程安全的
     *
     * 对于复合操作也需要线程保护, 客户端加锁
     *  1. 迭代
     *  2. 导航， 根据一定的顺序寻找下一个元素
     *  3. 条件运算， 如put-if-absent
     *
     * 客户端加锁
     *  一定要搞清楚，安全容器的锁在哪儿
     *  如Vector, 锁队对象是Vector对象自身
     */
    @Test
    public void oldSchool() {
        Vector<String> vector = new Vector<>();
        Hashtable<String, String> hashtable = new Hashtable<>();
        Stack<String> stack = new Stack<>();
    }

    /**
     * 两个都是符合操作
     */
    static class UnsafeVectorHelpers {
        public static Object getLast(Vector list) {
            int lastIndex = list.size() - 1;
            return list.get(lastIndex);
        }

        public static void deleteLast(Vector list) {
            int lastIndex = list.size() - 1;
            list.remove(lastIndex);
        }

        /** 可能抛出ArrayOutOfIndexException */
        public static void doSomething(Vector list) {
            for (int i = 0; i < list.size(); i++) {
                foo(list.get(i));
            }
        }

        private static void foo(Object o) {
        }
    }

    /**
     * 注意synchronized的对象
     *  所谓的客户度加锁，也是锁container自身
     */
    static class SafeVectorHelpers {
        public static Object getLast(Vector list) {
            synchronized (list) {
                int lastIndex = list.size() - 1;
                return list.get(lastIndex);
            }
        }

        public static void deleteLast(Vector list) {
            synchronized (list) {
                int lastIndex = list.size() - 1;
                list.remove(lastIndex);
            }
        }

        /**
         * 效率比较差
         *  一种替代方法是复制一个容器, 如果foo耗时较长，复制一个容器效率可能更高
         *  需要权衡一下是添加锁，或者是复制容器
         */
        public static void doSomething(Vector list) {
            synchronized (list) {
                for (int i = 0; i < list.size(); i++) {
                    foo(list.get(i));
                }
            }
        }

        private static void foo(Object o) {
        }
    }


    class HiddenIterator {
        @GuardedBy("this") private final Set<Integer> set = new HashSet<>();
        public synchronized void add(Integer i) {
            set.add(i);
        }
        public synchronized void remove(Integer i) {
            set.remove(i);
        }
        public void addTenThings() {
            Random r = new Random();
            for (int i = 0; i < 10; i++)
                add(r.nextInt());

            // 这里调用了toString, 容器的toString又会迭代调用
            // 可能会出现ConcurrentModificationException
            System.out.println("DEBUG: added ten elements to " + set);
        }

        public void foo() {
            /**
             * 容器的hashCode, equals也会有迭代
             * containAll, removeAll, retainAll
             * 把容器作为参数的方法 也已有迭代
             */
        }
    }

    @Test
    public void smoke() {
        // Threadsafe HashMap
        ConcurrentHashMap<String, String> hashMap = new ConcurrentHashMap<>();

        // Good performance in case of heavy read / few write
        // far more better than Vector
        CopyOnWriteArrayList<String> strings = new CopyOnWriteArrayList<>();

        ConcurrentLinkedQueue<String> linkedQueue = new ConcurrentLinkedQueue<>();

        BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>();

        ConcurrentSkipListMap<String, String> skipListMap = new ConcurrentSkipListMap<>();
    }

    // Performance not good containers
    // Implemented by synchronized
    @Test
    public void smoke2() {
        // Implemented by synchronized, performance is not good
        Map<String, String> map = Collections.synchronizedMap(new HashMap<>());

        List<String> list = Collections.synchronizedList(new ArrayList<>());
    }
}
