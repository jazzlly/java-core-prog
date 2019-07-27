package com.ryan.java.demo.concurrent;

import org.junit.Test;

/**
 * 两个线程之间通过wait, notify进行通讯
 *
 * 必须使用synchronized保护同步的object
 */
public class C011ThreadWait {
    static final Object lock = new Object();

    static class  MyRunnable implements Runnable {
        @Override
        public void run() {
            synchronized (lock) {  // 如果不加synchronized 就抛 IllegalMonitorStateException

                /** IllegalMonitorStateException
                 * Thrown to indicate that a thread has attempted to wait on an
                 * object's monitor or to notify other threads waiting on an object's
                 * monitor without owning the specified monitor.
                 */
                try {
                    System.out.println("begin waiting ...");
                    lock.wait();
                    System.out.println("after waiting ...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Test
    public void waitNotifyExample() throws InterruptedException {
        Thread thread1 = new Thread(new MyRunnable());
        Thread thread2 = new Thread(new MyRunnable());

        Thread thread3 = new Thread(() -> {
            while (true) {
                synchronized (lock) {
                    System.out.println("notify ....");
                    lock.notify();
                }

                try {
                    Thread.sleep(2_000);
                } catch (InterruptedException e) {
                    System.out.println("Thread interrupted");
                    break;
                }
            }
        });

        thread1.start();
        thread2.start();

        Thread.sleep(2_000);
        thread3.start();

        Thread.sleep(3_000);
        thread3.interrupt();

        thread1.join();
        thread2.join();
        thread3.join();
    }

    @Test
    public void waitNotifyAllExample() throws InterruptedException {
        Thread thread1 = new Thread(new MyRunnable());
        Thread thread2 = new Thread(new MyRunnable());

        Thread thread3 = new Thread(() -> {
            synchronized (lock) {
                System.out.println("notify ....");
                lock.notifyAll();
            }
        });

        thread1.start();
        thread2.start();

        Thread.sleep(2_000);
        thread3.start();

        thread1.join();
        thread2.join();
        thread3.join();
    }
}
