package com.ryan.java.demo.concurrent.lock;

import org.junit.Test;

import java.util.concurrent.locks.LockSupport;

/**
 * 线程的另一种阻塞机制
 *
 * 调试的时候容易看到线程的状态信息
 * LockSupport.park(), LockSupport.unpark(thread t)
 *
 * 特点：
 * 不用获取锁
 * unpark可以在park之前执行
 * 不会抛出interruptedException
 *
 * 如果被中断，park会直接返回，可通过Thread.interrupted()检查到
 */
public class C41LockSupport {

    class Task implements Runnable {

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() +
                    ": before park ...");
            LockSupport.park();

            // park() does not throw exception, but just return
            if (Thread.interrupted()) {
                System.out.println(Thread.currentThread().getName() +
                        ": interrupted!");
                return;
            }

            System.out.println(Thread.currentThread().getName() +
                    ": after park ...");
        }
    }

    @Test
    public void smoke() throws InterruptedException {
        Thread thread1 = new Thread(new Task(), "task1");
        Thread thread2 = new Thread(new Task(), "task2");

        thread1.start();
        thread2.start();

        Thread.sleep(5_000);

        LockSupport.unpark(thread1);

        Thread.sleep(2_000);

        LockSupport.unpark(thread2);

        thread1.join();
        thread2.join();
    }

    @Test
    public void parkInterrupted() throws InterruptedException {
        Thread thread1 = new Thread(new Task(), "task1");
        Thread thread2 = new Thread(new Task(), "task2");

        thread1.start();
        thread2.start();

        Thread.sleep(3_000);

        thread1.interrupt(); // park() will be interrupted!

        Thread.sleep(3_000);

        LockSupport.unpark(thread2);

        thread1.join();
        thread2.join();
    }
}
