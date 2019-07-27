package com.ryan.java.demo.concurrent.lock;

import org.junit.Test;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Condition和object的wait，notify方法类似
 *  配置ReentrantLock使用
 *
 *  注意，一般需要使用lock.lockInterruptly 并处理 interrupt异常
 *      await()是会被中断的
 */
public class C28Condition {

    @Test
    public void conditionAwait_thenSignal() throws InterruptedException {
        ConditionRunnable runnable = new ConditionRunnable();
        Thread thread1 = new Thread(runnable);
        Thread thread2 = new Thread(runnable);
        thread1.start();
        thread2.start();

        Thread.sleep(2_000);
        thread1.interrupt();

        Thread.sleep(2_000);
        runnable.signal();

        thread1.join();
        thread2.join();
    }

    @Test
    public void conditionAwait_thenInterruptThread() throws InterruptedException {
        ConditionRunnable runnable = new ConditionRunnable();
        Thread thread = new Thread(runnable, "condition-interrupted");
        thread.start();

        Thread.sleep(2_000);
        thread.interrupt();
        thread.join();
    }
}

class ConditionRunnable implements Runnable {

    private static final ReentrantLock lock = new ReentrantLock();
    // Condition总是和ReentrantLock绑定
    private static final Condition condition = lock.newCondition();


    public void signal() {
        lock.lock();
        try {
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void run() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() +
                    ": before condition await ...");
            condition.await();
            // condition.awaitUninterruptibly();  不可中断的await
            System.out.println(Thread.currentThread().getName() +
                    ": after condition await!");
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() +
                    ": interrupted");
        } finally {
            lock.unlock();
        }
    }
}