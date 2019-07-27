package com.ryan.java.demo.concurrent;

import lombok.Setter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 两个线程并发add一个list
 * synchronized方式可以保证值的正确
 */
public class C026ArrayListConcurrentErr {
    @Test
    public void async_valueNotCorrect() throws InterruptedException {
        Thread thread = new Thread(new ArrayListAddThread());
        Thread thread1 = new Thread(new ArrayListAddThread());

        ArrayListAddThread.syncAdd = false;

        thread.start();
        thread1.start();

        thread.join();
        thread1.join();

        System.out.println(ArrayListAddThread.list.size());
        // always less than the target? think why
        assertThat(ArrayListAddThread.list.size()).isLessThan(AddThread.ROUND * 2);

        // ArrayIndexOutOfBoundsException throwed randomlly
    }

    @Test
    public void sync_valueCorrect() throws InterruptedException {
        Thread thread = new Thread(new ArrayListAddThread());
        Thread thread1 = new Thread(new ArrayListAddThread());

        ArrayListAddThread.syncAdd = true;

        thread.start();
        //thread.join();

        thread1.start();

        thread.join();
        thread1.join();

        System.out.println(ArrayListAddThread.list.size());
        assertThat(ArrayListAddThread.list.size()).isEqualTo(ArrayListAddThread.ROUND * 2);
    }

}

@Setter
class ArrayListAddThread implements Runnable {
    public static final int ROUND = 500_000;

    public static boolean syncAdd = false;

    // static List<String> list = Collections.synchronizedList(new ArrayList<>());
    static List<String> list = new ArrayList<>();


    static void increase() {
        list.add(UUID.randomUUID().toString());
    }

    static void increaseSync() {
        synchronized (ArrayListAddThread.class) {
            list.add(UUID.randomUUID().toString());
        }
    }
    // same as increaseSync()
    static synchronized void increaseSync1() {
        list.add(UUID.randomUUID().toString());
    }

    @Override
    public void run() {
        for (int i = 0; i < ROUND; i++) {
            if (syncAdd) {
                increaseSync1();
            } else {
                increase();
            }
        }
    }
}