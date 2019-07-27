package com.ryan.java.demo.concurrent;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static com.ryan.java.demo.concurrent.LaunderThrowable.launderThrowable;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Using interrupt mechanizm to cancel task
 *
 * Not use Thread.stop() and Thread.suspend()
 *
 * 线程有一个boolean类型的中断状态
 * 当线程被中断时，系统会设置这个状态
 *
 * interrupt方法可以中断线程，
 * isInterrupted方法返回线程的中断状态
 * static Thread.interrupted方法清理中断状态，并返回之前的中断状态值
 *
 * 线程被中断时：
 *  1. 线程正好被block方法阻塞了
 *      当sleep被中断时，函数会抛出InterruptedException
 *      同时interrupted状态会被清除
 *
 *      处理策略：
 *          1.1 向调用者抛出InterruptedException
 *          1.2 进行部分中断处理工作，然后恢复中断状态
 *              上层调用者可以检测到中断状态，并进行后续处理
 *
 *  2. 如果线程中没有block方法
 *      线程的中断状态会被设置。然后直到线程的逻辑手动检测到中断状态
 *      处理完中断后，可通过static Thread.interrupted()方法清理
 *      中断状态
 *
 * Thread.interrupt()方法：
 *  向目标线程发送中断请求
 *  目标线程在方便的时候，一个取消点，中断自己
 *      取消点包括对InterruptedException的处理逻辑
 *      对Thread.isInterrupted()的检查
 *
 */
public class C004TaskCancel {
    // 当sleep被中断时，函数会抛出InterruptedException
    // 同时interrupted状态会被清除
    @Test
    public void interruptedSleepingThread() throws InterruptedException {
        Thread thread = new ThreadInterrupted();
        thread.start();

        Thread.sleep(3000);
        thread.interrupt();
        thread.join();
    }

    /**
     * 在catch中抛出了异常，finally也是会被执行的
     */
    @Test
    public void testTryFinal() {
        try {
            System.out.println("try");
            throw new IllegalStateException("exception in try");
        } catch (IllegalStateException e) {
            System.out.println("catch");
            throw e;
        } finally {
            System.out.println("finally!");
        }
    }
}

/**
 * 好的实践：线程取消
 *  1. 线程的循环条件里判读isInterrupted()
 *  2. 线程中处理InterruptedException
 *
 *  有多个检查点，但是只有一个退出点
 */
class ThreadInterrupted extends Thread {
    @Override
    public void run() {
        // 1. cancellation point 1， 退出点
        while (!Thread.currentThread().isInterrupted()) {
            try {
                System.out.println(
                        Thread.currentThread().getName() + ": sleeping ...");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // 2. cancellation point 2, 检查点

                // e.printStackTrace();
                // 当sleep被中断时，函数会抛出InterruptedException
                // 同时interrupted状态会被清除
                System.out.println(
                        Thread.currentThread().getName() + " was interrupted!");
                System.out.println(
                        Thread.currentThread().getName() + ", isInterrupted(): " +
                                Thread.currentThread().isInterrupted());

                // 进行中断处理 ...
                System.out.println(Thread.currentThread().getName() +
                        " is handling interrupt ...");

                // 恢复中断状态
                Thread.currentThread().interrupt();
                /**
                 * 这里有两个策略：
                 * 1. 恢复中断
                 * 2. 抛出InterruptedException
                 * 3. 退出线程
                 */

                // 到下一个检查点在退出，好的实践是最好只有一个退出点

                // break;
            }
        }
    }
}
/**
 * Using a volatile field to hold cancellation state
 */
@ThreadSafe
class PrimeGenerator implements Runnable {
    private static ExecutorService exec = Executors.newCachedThreadPool();

    @GuardedBy("this") private final List<BigInteger> primes
            = new ArrayList<BigInteger>();

    private volatile boolean cancelled;

    public void run() {
        BigInteger p = BigInteger.ONE;
        while (!cancelled) {
            p = p.nextProbablePrime();
            synchronized (this) {
                primes.add(p);
            }
        }
    }

    public void cancel() {
        cancelled = true;
    }

    public synchronized List<BigInteger> get() {
        return new ArrayList<BigInteger>(primes);
    }

    static List<BigInteger> aSecondOfPrimes() throws InterruptedException {
        PrimeGenerator generator = new PrimeGenerator();
        exec.execute(generator);
        try {
            SECONDS.sleep(1);
        } finally {
            generator.cancel();
        }
        return generator.get();
    }
}

/**
 * Interrupting a task in a dedicated thread
 * 延时中断一个线程，并重新抛出异常
 */
class TimedRun2 {
    private static final ScheduledExecutorService cancelExec = newScheduledThreadPool(1);

    public static void timedRun(final Runnable r,
                                long timeout, TimeUnit unit)
            throws InterruptedException {
        class RethrowableTask implements Runnable {
            private volatile Throwable t;

            public void run() {
                try {
                    r.run();
                } catch (Throwable t) {
                    this.t = t;
                }
            }

            void rethrow() {
                if (t != null)
                    throw launderThrowable(t);
            }
        }

        RethrowableTask task = new RethrowableTask();
        final Thread taskThread = new Thread(task);
        taskThread.start();
        cancelExec.schedule(new Runnable() {
            public void run() {
                taskThread.interrupt();
            }
        }, timeout, unit);
        taskThread.join(unit.toMillis(timeout));
        task.rethrow();
    }
}


/**
 * Cancelling a task using Future
 * 这个最简洁了
 */
class TimedRun {
    private static final ExecutorService taskExec = Executors.newCachedThreadPool();

    public static void timedRun(Runnable r,
                                long timeout, TimeUnit unit)
            throws InterruptedException {
        Future<?> task = taskExec.submit(r);
        try {
            task.get(timeout, unit);
        } catch (TimeoutException e) {
            // task will be cancelled below
        } catch (ExecutionException e) {
            // exception thrown in task; rethrow
            throw launderThrowable(e.getCause());
            // fixme: Finally会在异常抛出前执行！
            // task无论如何都会被cancel的
        } finally {
            // Harmless if task already completed
            task.cancel(true); // interrupt if running
        }
    }
}
