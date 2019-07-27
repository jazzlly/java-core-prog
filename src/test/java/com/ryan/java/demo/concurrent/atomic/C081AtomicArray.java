package com.ryan.java.demo.concurrent.atomic;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerArray;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 能够原子的更新数组中的各个值
 *
 *  底层封装了一个int[], 该数组无法改变，没有add方法
 *  使用Unsafe方法实现CAS操作
 */
public class C081AtomicArray {

    private static AtomicIntegerArray array = new AtomicIntegerArray(10000);

    @Before
    public void setUp() {
        for (int i = 0; i < array.length(); i++) {
            array.set(i, 0);
        }
    }

    @Test
    public void smoke() throws InterruptedException {
        MyRunnable myRunnable = new MyRunnable();
        ExecutorService service = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 40; i++) {
            service.submit(myRunnable);
        }

        service.shutdown();
        service.awaitTermination(10, TimeUnit.SECONDS);

        assertThat(array).containsOnly(40);
    }

    // concurrent modify array values
    static class MyRunnable implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < array.length(); i++) {
                array.getAndIncrement(i);
            }
        }
    }
}
