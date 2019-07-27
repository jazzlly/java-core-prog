package com.ryan.java.demo.concurrent.pool;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.*;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * ThreadFactory可以用于定制线程的属性，
 *  如名称，daemon等等
 *  可以指定UncaughtExceptionHandler
 */
@Slf4j
public class C60ThreadFactory {

    public static int runnableCount = 0;
    public static int runnable1Count = 0;

    static Runnable runnable = () -> {
        log.info("");
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

    @Before
    public void setUp() throws Exception {
        synchronized (C60ThreadFactory.class) {
            runnableCount = 0;
        }
    }

    static ThreadFactory threadFactory = new ThreadFactory() {
        volatile int count = 0;

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "ryan-thread-" + count);
            synchronized (this) {
                count++;
            }
            return thread;
        }
    };

    @Test
    public void smoke() throws InterruptedException {
        ExecutorService service = new ThreadPoolExecutor(
                3,3, 0, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(), threadFactory);
        service.submit(runnable);
        service.submit(runnable);
        service.submit(runnable);
        service.submit(runnable);

        Thread.sleep(2_000);
        service.shutdown();
        service.awaitTermination(5, TimeUnit.SECONDS);

        assertThat(runnableCount).isEqualTo(4);
    }
}
