package com.ryan.java.demo.concurrent.lock;

import org.junit.Test;

import java.util.concurrent.Semaphore;

/**
 * Semaphore允许多个线程同时访问资源
 *
 * aquire()可以被中断，中断后，不能在catch中调用release方法
 * 如果没有调用semaphore.aquire, 也可以调用semaphore.release
 *
 * Semaphore可以方便的实现有界队列
 */
public class C029SemaphoreDemo {
    @Test
    public void smoke() throws InterruptedException {
        SemaphoreRunable runable = new SemaphoreRunable();

        Thread thread1 = new Thread(runable, "thread1");
        Thread thread2 = new Thread(runable, "thread2");
        Thread thread3 = new Thread(runable, "thread3");
        Thread thread4 = new Thread(runable, "thread4");

        thread1.start();
        Thread.sleep(2_000);
        thread2.start();
        Thread.sleep(2_000);
        thread3.start();
        Thread.sleep(2_000);
        thread4.start();

        Thread.sleep(5_000);
        thread3.interrupt(); // await interrupted!
        Thread.sleep(5_000);
        thread4.interrupt(); // await interrupted!

        Thread.sleep(2_000);
        thread1.interrupt();
        thread2.interrupt();

        thread1.join();
        thread2.join();
        thread3.join();
        thread4.join();
    }
}

class SemaphoreRunable implements Runnable {

    // 允许3个线程同时访问资源
    public static final Semaphore SEMAPHORE = new Semaphore(3);

    @Override
    public void run() {
        try {
            System.out.println(Thread.currentThread().getName() +
                    ": before acquire ...: " + SEMAPHORE.availablePermits());
            SEMAPHORE.acquire();
            try {
                System.out.println(Thread.currentThread().getName() +
                        ": after acquire ...");

                Thread.sleep(50_000);
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() +
                        ": sleep interrupted!");
            } finally {
                SEMAPHORE.release();
            }
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() +
                    ": await interrupted!");
            System.out.println("available permits: " + SEMAPHORE.availablePermits());
        }
    }
}
