package com.ryan.java.demo.concurrent.exception;

import com.ryan.java.demo.concurrent.pool.C52ThreadPoolExecutorRefusePolicy;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.*;

/**
 * ExecutorService submit的任务中抛出的异常，是无法被打印出来的
 *  除非对返回的future .get()一下
 *
 *  ExecutorService.execute()方法提交的任务，异常是可以被捕获的
 */
@Slf4j
public class C62ThreadStack {
    public static int runnableCount = 0;
    public static int runnable1Count = 0;

    static Runnable runnable = () -> {
        log.info("");
        log.info(1/0 + "");
        System.out.println(Thread.currentThread().getName() +
                ": begin ...");
        try {
            Thread.sleep(2_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        synchronized (C52ThreadPoolExecutorRefusePolicy.class) {
            runnableCount++;
        }

        System.out.println(Thread.currentThread().getName() +
                ": end!");
    };

    static Runnable exceptionRunnable = new Runnable() {
        @Override
        public void run() {
            throw new IllegalArgumentException("foo bar wahaha!");
        }
    };

    @Test
    public void submitHasNoExceptionInfo() throws InterruptedException {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(runnable);

        service.shutdown();
        service.awaitTermination(10, TimeUnit.SECONDS);
        // fixme: no exception!
    }

    @Test
    public void submitHasNotExceptionInfo1() throws InterruptedException {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(exceptionRunnable);

        service.shutdown();
        service.awaitTermination(5, TimeUnit.SECONDS);
    }

    @Test
    public void submitAndGet() throws ExecutionException, InterruptedException {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future future = service.submit(exceptionRunnable);
        future.get();

        service.shutdown();
        service.awaitTermination(5, TimeUnit.SECONDS);
    }

    @Test
    public void executeHasExceptionInfo() throws InterruptedException {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(runnable);

        service.shutdown();
        service.awaitTermination(5, TimeUnit.SECONDS);
    }

    @Test
    public void executeHasExceptionInfo1() throws InterruptedException {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(exceptionRunnable);

        service.shutdown();
        service.awaitTermination(5, TimeUnit.SECONDS);
    }
}
