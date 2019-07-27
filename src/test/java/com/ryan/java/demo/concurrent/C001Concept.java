package com.ryan.java.demo.concurrent;

import org.junit.Test;

public class C001Concept {
    /**
     * 并发： concurrency, 一个cpu, 串行交替执行
     * 并行： parallelism, 多个cpu, 并行执行
     *
     * 临界区：需要受到保护的资源，如数据，硬件资源
     *
     *
     * AVS：
     *
     * 原子性：
     *  long类型在32bit jvm的操作不是原子的
     *  ++操作不是原子的
     *      read-modify-write
     *
     *      check-then-act
     *
     * 可见性：
     *  线程1对全局变量v的修改，线程2是否立即可见？
     *
     * 有序性：
     *  有序的代码可能并发乱序执行
     *
     */

    /**
     * 线程安全：
     *
     *  如果一个类没有状态变量，包括成员变量，静态变量，这个类是线程安全的
     *      局部变量保存在堆栈上，是线程安全的
     *
     *  如果状态变量都是不变的，则该类是线程安全的
     *
     *  如果多个原子变量之间是有关联的，设置这些变量也应该在一个锁里面
     */

    /**
     * Synchronized
     *  内部锁或监视器锁， intrinsic/monitor lock
     *      进入synchronized块之前，自动获取锁
     *      退出之前，无论是正常退出或异常退出，都自动释放锁
     *
     *  内部锁是互斥锁
     *  内部锁是可重入的，锁是基于每个线程的，而不是每次调用的
     *      和ReentrantLock行为一致
     *
     *
     */

    /**
     * 过期值 vs 错误值
     *  在没有同步的情况下，线程可能读取到一个过期值
     *
     *  但是对于非volatile的64位原始类型，long， double可能读取到一个乱码
     *  32bit的jvm运行将64位的读和写操作划分为两个32bit操作
     */
    @Test
    public void advice() {
        /**
         * 提高性能的建议；
         * 1. 减小锁持有的时间
         * 2. 减小锁的粒度，
         *      对比synchronizedHashMap和ConcurrentHashMap
         *      前者是全局锁
         *      后者是分段锁
         * 3. 读写分离锁
         * 4. 锁的功能分离
         *  如BlockingQueue中的put和take使用了两个锁
         *
         *  耗时的计算，网络IO操作，或控制台操作，尽量不要占用锁
         *
         */
    }

    /**
     * JVM对锁的优化：
     * 1. 锁偏向
     *  如果线程获取了锁，则锁进入偏向模式
     *  如果该线程再次请求锁，无需再进行同步操作
     *  适合于锁竞争不太激烈的场合
     *
     * 2. 轻量级锁
     *  如果偏向锁失败，虚拟机不会立即挂起进程, 使用轻量级锁
     *  请求轻量级锁失败，当前线程的锁膨胀成重量级锁
     *
     * 3. 自旋锁
     * 虚拟机会让当前线程做几个空的循环
     * 若干次循环后，可以获取锁，就进入临界区
     * 否则，才会在操作系统层面挂起系统
     *
     */
    @Test
    public void jdkLockOptimize() {
    }


    /**
     * fixme: checklist 模式
     * check-then-change
     * put-if-absent
     */

    /**
     * fixme: 设计思路
     * 如何给已有类添加同步机制
     *  代理模式：参考Collections.synchronizedList/Map, ...
     *      Collections.unmodifiedList/Map, ...
     */
}
