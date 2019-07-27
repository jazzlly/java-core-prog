package com.ryan.java.demo.concurrent;

import org.junit.Test;

/**
 * 线程组用于线程的分类和管理
 *  最好给线程和线程组起有意义，能看懂的名字
 */
public class C20ThreadGroup {

    @Test
    public void smoke() throws InterruptedException {
        ThreadGroup threadGroup = new ThreadGroup("foo thread group");
        Thread foo = new Thread(threadGroup, new MyRunnable(), "foo");
        Thread bar = new Thread(threadGroup, new MyRunnable(), "bar");

        foo.start();
        bar.start();

        Thread.sleep(10_000);
        foo.interrupt();
        bar.interrupt();

        foo.join();
        bar.join();
    }
}

class MyRunnable implements Runnable {

    @Override
    public void run() {
        while (true) {
            System.out.println(Thread.currentThread().getName() + ": running ...");
            System.out.println("Group name: " + Thread.currentThread().getThreadGroup().getName());
            try {
                Thread.sleep(1_500);
            } catch (InterruptedException e) {
                System.out.println("interrupted ...");
                break;
            }
        }
    }
};
