package com.ryan.java.demo.concurrent;

import org.junit.Test;

import java.util.concurrent.BlockingQueue;

/**
 * 线程是可以被中断的
 *
 *  线程的状态时blocked, waiting, time waiting
 *      等待io操作，锁可用， 。。。
 *
 *  如何处理InterruptedException
 *      1. 传递，将异常传递给调用者
 *          不捕获这个异常，直接抛出
 *          捕获这个异常，清理状态，然后再抛出
 *
 *      2. 恢复中断
 *          Thread.currentThread.interrupt()
 *              将会清理现成的interrupt状态，并向上一级抛出异常？
 *          比如在一个sleep中被中断，有可能sleep函数的上层调用者还有可以被中断的函数
 *          可以将这个中断抛出去？
 */
public class C010ThreadInterrupt {

    /**
     * 通过Thread.interrupted()方法检测线程是否被中断
     *
     * @throws InterruptedException
     */
    @Test
    public void CheckInterruptMannaully() throws InterruptedException {

        Thread thread = new Thread(() -> {
            while (true) {

                if (Thread.currentThread().interrupted()) {
                    System.out.println("Thread interrupted");
                    return;
                }
                Thread.yield();
            }
        });

        thread.start();
        Thread.sleep(2_000);
        thread.interrupt();  // 中断线程
        thread.join();
    }

    /**
     * Thread.sleep()方法是可以被中断的
     *
     * @throws InterruptedException
     */
    @Test
    public void sleepBeInterrupted() throws InterruptedException {
         Thread thread = new Thread(() -> {
             while (true) {
                 try {
                     Thread.sleep(10_000);
                 } catch (InterruptedException e) {
                     System.out.println("Thread is interrupted!");
                     // e.printStackTrace();
                     return;
                 }
             }
         });

        thread.start();
        Thread.sleep(2_000);
        thread.interrupt(); // will catch by Thread.sleep() in the thread
        thread.join();

    }


    /**
     * Object.wait()方法是可以被中断的
     * @throws InterruptedException
     */
    @Test
    public void waitBeInterrupted() throws InterruptedException {

        Thread thread1 = new Thread(() -> {
            final Object o = new Object();
            synchronized (o) {
                try {
                    System.out.println("Thread waiting...");
                    o.wait();
                    System.out.println("Thread waiting done!");
                } catch (InterruptedException e) {
                    System.out.println("Waiting interrupted!");
                }
            }
        });

        thread1.start();;
        Thread.sleep(2_000);
        thread1.interrupt();
        thread1.join();
    }
}


/**
 * TaskRunnable
 * <p/>
 * Restoring the interrupted status so as not to swallow the interrupt
 *
 * @author Brian Goetz and Tim Peierls
 */
class TaskRunnable implements Runnable {
    BlockingQueue<Task> queue;

    public void run() {
        try {
            processTask(queue.take());
        } catch (InterruptedException e) {
            // restore interrupted status
            Thread.currentThread().interrupt();
        }
    }

    void processTask(Task task) {
        // Handle the task
    }

    interface Task {
    }
}

