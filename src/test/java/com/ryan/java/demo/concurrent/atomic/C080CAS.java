package com.ryan.java.demo.concurrent.atomic;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Compare and Swap
 */
public class C080CAS {
    /**
     * There concept of CAS (Compare and Swap):
     *  Implemented by CPU hardware directive:
     *      lock_if_mp cmpxchgl
     *  CAS was supported by JDK 5.0
     *
     *  VEN
     *  1. variable: to be updated
     *  2. expect value:
     *  3. new value:
     *
     *  T cas() {
     *  if v == e
     *      v = n
     *  else
     *      // do nothing, other threads have already do it
     *  return v
     *  }
     *
     * common method:
     * [get, compare, ++, --, add ] and [++, --, add, set]
     *  compareAndSet()
     */

    /**
     * ABA问题：
     *  线程V的值任然为A吗？如果还是A, 则修改
     *      问题，V被修改为A, 然后B, 最后有修改成了A
     *
     *  问题变成：上次我观察后，V的值发生了变化吗？
     *  实现方式：添加一个版本号，修改引用之后，同时修改版本号
     *      AtomicStampedReference
     *      AtomicMarkableReference
     *
     *  ABA问题存在于垃圾回收算法中?
     */

    /**
     * 锁的问题：
     *  锁会导致线程的挂起和恢复，对于高并发的场景，这个会消耗很多cpu时间
     *  如果持有锁的线程优先级比较低，或导致高优先级的线程被阻塞
     *
     *  volatile仅仅保证了可见性，但是不保证原子性
     */

    /** Jiang Rui: Method for AtomicInteger
     */
    // public final boolean compareAndSet(int expect, int update) {
        // return unsafe.compareAndSwapInt(this, valueOffset, expect, update);
        /**
         * pubilic final native boolean compareAndSwapInt(
         *      Object o,
         *      long offset: offset of the int value to object header
         *      int expect,
         *      int update);
         */

        // The value to be set must be volatile
        // public native void putOrderedInt(Object o, long offset, int value);

        // The value to be set may not be volatile
        // public native void putIntVolatile(Object var1, long var2, int var4);
   // }

    static final AtomicInteger atomicInteger = new AtomicInteger();
    private static Callable<Integer> callable = () -> {
        for (int i = 0; i < 10000; i++) {
            atomicInteger.incrementAndGet();
            Thread.yield();
        }
        return atomicInteger.get();
    };

    /**
     * AtomicInteger is implemented by CAS
     */
    @Test
    public void atomicInteger() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(8);
        for (int i = 0; i < 8; i++) {
            service.submit(callable);
        }

        service.shutdown();
        service.awaitTermination(10, TimeUnit.SECONDS);

        assertThat(atomicInteger.get()).isEqualTo(80_000);
    }

    @Test
    public void casPattern() {
        /*
         while (true) {
                int old = value.get();
                if (value.compareAndSet(old, old + 1)) {
                    return;
                }
            }
        */
    }

    /**
     * Simulated CAS operation
     */
    @ThreadSafe
    class SimulatedCAS {
        @GuardedBy("this") private int value;

        public synchronized int get() {
            return value;
        }

        public SimulatedCAS(int value) {
            this.value = value;
        }

        public synchronized int compareAndSwap(int expectedValue,
                                               int newValue) {
            int oldValue = value;
            if (oldValue == expectedValue)
                value = newValue;
            return oldValue;
        }

        public synchronized boolean compareAndSet(int expectedValue,
                                                  int newValue) {
            return (expectedValue
                    == compareAndSwap(expectedValue, newValue));
        }
    }

    class CasCounter {
        private SimulatedCAS value = new SimulatedCAS(0);

        public int getValue() {
            return value.get();
        }

        public void increase() {
            while (true) {
                int old = value.get();
                if (value.compareAndSet(old, old + 1)) {
                    return;
                }
            }
        }
    }

    @Test
    public void testCasCounter() throws InterruptedException {
        final CasCounter casCounter = new CasCounter();
        ExecutorService service = Executors.newCachedThreadPool();

        Runnable runnable = () -> {
            for (int i = 0; i < 10000; i++) {
                casCounter.increase();
            }
        };

        for (int i = 0; i < 8; i++) {
            service.submit(runnable);
        }

        service.shutdown();
        service.awaitTermination(10, TimeUnit.SECONDS);

        assertThat(casCounter.getValue()).isEqualTo(80000);
    }
}
