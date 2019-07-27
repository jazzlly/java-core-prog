package com.ryan.java.demo.concurrent;

import org.junit.Test;

import java.util.concurrent.*;

public class C007ThreadException {

    // The thread pool is alive, after exception of thread
    // after the thread dead, pool will create new thread
    @Test
    public void smoke() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(4);

        for (int i = 0; i < 20; i++) {
            service.submit(new ExceptionRunnable());
        }
        Thread.sleep(3_000);
        service.shutdown();
        service.awaitTermination(10, TimeUnit.SECONDS);
    }

    /**
     * fixme: seems that the handler can not catch the exception in thread
     *  pool already handle the exception?
     * @throws InterruptedException
     */
    @Test
    public void handleExceptionInThread() throws InterruptedException {
        final Thread.UncaughtExceptionHandler handler = (t, e) -> {
            System.out.println(t.getName() + " got exception!");
            e.printStackTrace();
        };

        ThreadFactory factory = r -> {
            Thread thread = new Thread(r);
            thread.setUncaughtExceptionHandler(handler);
            return thread;
        };
        ExecutorService service = new ThreadPoolExecutor(
                4, 4,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), factory);

        for (int i = 0; i < 20; i++) {
            service.submit(new ExceptionRunnable());
        }
        Thread.sleep(3_000);
        service.shutdown();
        service.awaitTermination(10, TimeUnit.SECONDS);

    }
}

class ExceptionRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() +
                        ": got exception");
        throw new RuntimeException("muhaha");
        // fixme: the pooll can not see the exception
        // no log output!
    }
}
