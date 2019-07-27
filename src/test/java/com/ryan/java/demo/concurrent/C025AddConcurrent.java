package com.ryan.java.demo.concurrent;

import lombok.Setter;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 两个线程并发对一个变量++
 * synchronized方式可以保证值的正确
 */
public class C025AddConcurrent {
    @Test
    public void async_valueNotCorrect() throws InterruptedException {
        Thread thread = new Thread(new AddThread());
        Thread thread1 = new Thread(new AddThread());

        AddThread.syncAdd = false;

        thread.start();
        thread1.start();

        thread.join();
        thread1.join();

        System.out.println(AddThread.count);
        // always less than the target? think why
        assertThat(AddThread.count).isLessThan(AddThread.ROUND * 2);
    }

    @Test
    public void sync_valueCorrect() throws InterruptedException {
        Thread thread = new Thread(new AddThread());
        Thread thread1 = new Thread(new AddThread());

        AddThread.syncAdd = true;

        thread.start();
        thread1.start();

        thread.join();
        thread1.join();

        System.out.println(AddThread.count);
        assertThat(AddThread.count).isEqualTo(AddThread.ROUND * 2);
    }

}

@Setter
class AddThread implements Runnable {
    public static final int ROUND = 10_000_000;

    public static boolean syncAdd = false;

    static volatile int count = 0;
    static void increase() {
        count++;
    }

    static void increaseSync() {
        synchronized (AddThread.class) {
            count++;
        }
    }
    // same as increaseSync()
    static synchronized void increaseSync1() {
        count++;
    }

    @Override
    public void run() {
        for (int i = 0; i < ROUND; i++) {
            if (syncAdd) {
                increaseSync();
            } else {
                increase();
            }
        }
    }
}