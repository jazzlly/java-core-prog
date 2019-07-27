package com.ryan.java.demo.concurrent.lock;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.filter;

/**
 * ReentrantLock的特点：
 *  可以响应中断, 也可以不响应中断
 *  可以设置超时
 *  可以tryLock
 *
 * 内部锁的好处：
 *  简单
 *  不容易出错，ReentrantLock如果没有unlock...
 *
 * 总结
 *   ReentrantLock和Synchronized性能差异在java6之后就不大了
 *   需要如下高级特性的时候，才使用ReentrantLock
 *      可定时，可轮询，可中断，公平队列
 *
 * 线程被锁阻塞时，JVM一般会将这个线程挂起
 *
 * 可重入锁在jdk5时，性能明显好于synchronized。
 * 不过jdk6 synchronized的性能明显提高了
 *
 * 可重入锁一个线程可以多次调用，但也必须释放多次
 * 可重入锁比synchronized灵活，可以指定在哪儿lock，在哪儿unlock
 *
 * ReentranceLock是可以被中断的，提供了lockInterruptible方法
 *
 * tryLock：尝试获取锁，如果成功返回true，否则false。可以指定一个timeout
 *
 */
@Slf4j
public class C027ReenterLock {
    static ReentrantLock reentrantLock = new ReentrantLock();

    // 使用lock的模板代码，在finally中unlock
    @Test
    public void lockPattern() {
        Lock lock = new ReentrantLock();
        lock.lock();
        log.info("Lock lock!");
        try {
            log.info("do something in lock ...");
            throw new IllegalStateException("state error");
        } catch (Exception e) {
            log.info("restore exception");
        } finally {
            lock.unlock();
            log.info("Lock unlock!");
        }
    }

    // 使用lockInterruptibly()的模板代码
    // 1. lockInterruptibly()不用放在try里面
    // 2. 将InterruptedException放到函数的signature中
    // 3. 在finally中进行unlock
    @Test
    public void lockInterruptPattern() throws InterruptedException {
        Lock lock = new ReentrantLock();
        lock.lockInterruptibly();
        log.info("lock lock!");
        try {
            log.info("doing sth ...");
            throw new IllegalStateException("error");
        } catch (IllegalStateException e) {
            log.warn("got exception", e);
        } finally {
            lock.unlock();
            log.info("lock unlock!");
        }
    }

    /**
     * Lock.lockInterruptibly()函数被InterruptedException中断后：
     *  不能再去unlock了
     *  如果再去unlock, 就会抛出java.lang.IllegalMonitorStateException
     *
     *  IllegalMonitorStateException
     *      Thrown to indicate that
     *          1. a thread has attempted to wait on an object's monitor
     *              比如没有synchronized, 就去wait
     *          2. or to notify other threads waiting on an object's
     *              monitor without owning the specified monitor.
     *              比如没有synchronized, 就去notify
     *              unlock就是去notify
     */
    @Test(expected = java.lang.IllegalMonitorStateException.class)
    public void testUnlockWhenCatchInterruptException() {
        final Thread main = Thread.currentThread();
        final CountDownLatch latch = new CountDownLatch(1);

        Runnable runnable = () -> {
            try {
                log.info("thread begin ...");
                reentrantLock.lockInterruptibly();
                log.info("lock lock!");
                try {
                    log.info("working ...");
                    latch.countDown();
                    Thread.sleep(1000);
                    main.interrupt();
                    Thread.sleep(50_000);
                } catch (Exception e) {
                    log.info("got error!");
                } finally {
                    reentrantLock.unlock();
                    log.info("lock unlock!");
                }
            } catch (InterruptedException e) {
                log.info("thread interrupt!");
           }
        };

        Thread thread1 = new Thread(runnable, "test-lock-thread1");
        thread1.start();

        try {
            latch.await();
        } catch (InterruptedException e) {
            log.warn("latch interrupte!");
        }

        try {
            reentrantLock.lockInterruptibly();
        } catch (InterruptedException e) {
            log.info("lock interrupted!");

            // fixme: throw IllegalMonitorStateException here!
            // ReentrantLock被中断后，就不要去unlock了，否则就会有异常
            reentrantLock.unlock();
        }
    }

    @Test(expected = InterruptedException.class)
    public void testUnlockAfterThrowInterruptException2() throws InterruptedException {
        final Thread main = Thread.currentThread();
        final CountDownLatch latch = new CountDownLatch(1);

        Thread thread1 = new Thread(() -> {
            try {
                latch.await();
                Thread.sleep(500);
                main.interrupt();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "test-lock-thread1");
        thread1.start();

        while (true) {
            System.out.println("main thread: before lock");
            reentrantLock.lockInterruptibly();
            System.out.println("main thread: after lock");

            try {
                System.out.println("main thread: got lock");
                // 无论抓住异常，还是抛出异常，finnaly中unlock都ok
                latch.countDown();
                Thread.sleep(2000);
                // fixme: sleep()抛出异常后，还是会到下面finally中去unlock
                // fixme: unlock完成后，再从finally中把异常抛出整个函数
            } finally {
                System.out.println("main thread: unlock");
                reentrantLock.unlock();
                System.out.println("main thread: unlock done!");
            }
        }
    }

    @Test
    public void reentrantLockDemo() throws InterruptedException {
        ReenterLockRunnable runnable = new ReenterLockRunnable();
        Thread thread1 = new Thread(runnable);
        Thread thread2 = new Thread(runnable);

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        assertThat(ReenterLockRunnable.getI()).isEqualTo(
                ReenterLockRunnable.ROUND * 2);
    }

    /**
     * ReenterLock是可以被中断的
     * @throws InterruptedException
     */
    @Test
    public void lockInterruptibleDemo() throws InterruptedException {
        InterruptLockRunnable runnable = new InterruptLockRunnable();
        Thread thread1 = new Thread(runnable, "thread1");
        Thread thread2 = new Thread(runnable, "thread2");

        thread1.start();
        Thread.sleep(5_000);

        thread2.start();  // thread 2被锁住了 lockInterruptible
        Thread.sleep(5_000);

        // thread2.interrupt();
        thread1.interrupt();
        Thread.sleep(2_000);

        // thread1.interrupt(); // sleep被interrupt
        thread1.join();
        thread2.join();
    }

    @Test
    public void lockTimeoutDemo() throws InterruptedException {
        LockTimeoutRunnable runnable = new LockTimeoutRunnable();
        Thread thread1 = new Thread(runnable, "thread1");
        Thread thread2 = new Thread(runnable, "thread2");

        thread1.start();
        Thread.sleep(2_000);
        thread2.start();

        thread2.join();     // thread2 tryLock 5 seconds timeout
        thread1.interrupt();
        thread1.join();
    }

    @Test
    public void fairLockDemo() {
        ReentrantLock fairLock = new ReentrantLock(true);
        // fairlock有一个队列，当锁可以用时，队列最前面的线程将得到锁
        // fixme: fair lock在高并发下，会损失很大的性能

        ConcurrentHashMap<String, String> hashMap = new ConcurrentHashMap<>();

    }

    @Test
    public void tryLockDemo() throws InterruptedException {
        final ReentrantLock lock = new ReentrantLock();
        Thread thread = new Thread(() -> {
            while (true) {
                if (lock.tryLock()) {
                    try {
                        System.out.println("got lock, do something ...");
                    } finally {
                        lock.unlock();
                        break;
                    }
                }
                try {
                    System.out.println("can not get lock, sleep");
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("thread interrupted");
                }
            }
        });

        lock.lock();
        thread.start();

        Thread.sleep(2000);
        lock.unlock();
        thread.join();
    }
}

class ReenterLockRunnable implements Runnable {

    private static int i = 0;
    private static ReentrantLock lock = new ReentrantLock();
    public static final int ROUND = 1_000_000;

    public static int getI() {
        return i;
    }

    @Override
    public void run() {

        for (int j = 0; j < ROUND; j++) {
            try {
                lock.lockInterruptibly();  // same as synchronized
                lock.lockInterruptibly();  // 在一个线程中，可以被多次调用
                i++;
                lock.unlock();
                lock.unlock();
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() +
                        ": interrupted!");
            }
        }
    }
}

@Slf4j
class InterruptLockRunnable implements Runnable {

    private static ReentrantLock lock = new ReentrantLock();

    @Override
    public void run() {
        try {
            log.info("before lock ...");
            lock.lockInterruptibly();
            log.info("after lock ...");

            doSomething();
            log.info("after sleep ...");

        } catch (InterruptedException e) {
            log.info("lock interrupted ! ...");
            log.info("lock is locked: " + lock.isLocked());
        }
    }

    private void doSomething() {
        try {
            Thread.sleep(500_000);
        } catch (InterruptedException e) {
            log.info("sleep interrupted ...");
            log.info("lock is locked: " + lock.isLocked());
            Thread.currentThread().interrupt();
        }

    }
}

class LockTimeoutRunnable implements Runnable {

    private static ReentrantLock lock = new ReentrantLock();
    @Override
    public void run() {

        try {
            System.out.println(Thread.currentThread().getName() + ": before lock ...");
            boolean getLock = lock.tryLock(5, TimeUnit.SECONDS);
            if (!getLock) {
                System.out.println("can not get lock, return ...");
                return;
            }
            System.out.println(Thread.currentThread().getName() + ": after lock ...");

            Thread.sleep(500_000);
            System.out.println(Thread.currentThread().getName() + ": after sleep ...");

        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + ": interrupted ! ...");
        }
    }
}


