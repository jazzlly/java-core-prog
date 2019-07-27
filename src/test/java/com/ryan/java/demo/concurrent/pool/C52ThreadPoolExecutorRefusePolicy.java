package com.ryan.java.demo.concurrent.pool;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * fixme: 业务代码中，一定要处理RejectedExecutionException
 *
 * 四种拒绝策略：
 *  线程池满，等待队列满
 *
 *  1. AbortPolicy: 直接抛出异常
 *      abort policy + 有界队列 是一种failed fast的策略
 *      直接返回异常，通知调用者服务器现在忙, 返回限流:499, ...
 *      由调用者去处理重试逻辑
 *
 *  2. CallerRunsPolicy:
 *      直接在调用者的线程中运行任务
 *      会导致web服务的处理线程直接运行任务
 *      最后处理线程用光，web server开始拒绝请求
 *
 *      对于单纯的客户端的服务，可以采取CallerRunsPolicy
 *          但是要加上监控
 *
 *  3. DiscardOldestPolicy: 将丢弃最老的一个任务，然后提交新的任务
 *
 *  4. DiscardPolicy: 丢弃无法处理的任务
 */
@Slf4j
public class C52ThreadPoolExecutorRefusePolicy {
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

    static Runnable runnable1 = () -> {
        log.info("");
        System.out.println(Thread.currentThread().getName() +
                ": runable 1 begin ...");
        try {
            Thread.sleep(2_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        synchronized (C52ThreadPoolExecutorRefusePolicy.class) {
            runnable1Count++;
        }

        System.out.println(Thread.currentThread().getName() +
                ": end!");
    };

    @Before
    public void setUp() throws Exception {
        synchronized (C52ThreadPoolExecutorRefusePolicy.class) {
            runnableCount = 0;
            runnable1Count = 0;
        }
    }

    @Test
    public void smoke() {
        Executors.newSingleThreadExecutor();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        /*
        public static ExecutorService newSingleThreadExecutor() {
        return new FinalizableDelegatedExecutorService
            (new ThreadPoolExecutor(1, 1,
                                    0L, TimeUnit.MILLISECONDS,
                                    new LinkedBlockingQueue<Runnable>()));  // 无界队列
                                    abort策略
            }
         */
        Executors.newFixedThreadPool(4);
        /*
        public static ExecutorService newFixedThreadPool(int nThreads) {
        return new ThreadPoolExecutor(nThreads, nThreads,
                                      0L, TimeUnit.MILLISECONDS,
                                      new LinkedBlockingQueue<Runnable>()); // 无界队列
                                      abort策略
        }
         */
        Executors.newCachedThreadPool();
        /*
        public static ExecutorService newCachedThreadPool() {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                      60L, TimeUnit.SECONDS,
                                      new SynchronousQueue<Runnable>());
                                      abort策略
    }
         */

    }

    @Test
    public void testAbortPolicy() {
        Executors.newFixedThreadPool(5);

        ExecutorService service = new ThreadPoolExecutor(
                1,1, 0, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(1),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

        service.submit(runnable);  // handle by core thread
        service.submit(runnable);  // waiting in working queue

        try {
            service.submit(runnable);   // no core thread, no place in working queue
                                        // AbortPolicy will throw rejected execution
        } catch (RejectedExecutionException e) {
            e.printStackTrace();
            return;
        }

        assertThat(true).isFalse();
    }

    @Test
    public void testCallerRunsPolicy() throws InterruptedException {
        ExecutorService service = new ThreadPoolExecutor(
                1,1, 0, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());

        service.submit(runnable);  // handle by core thread
        service.submit(runnable);  // waiting in working queue

        service.submit(runnable);   // no core thread, no place in working queue
                                    // run in main thread
        Thread.sleep(2_000);
        service.shutdown();
        service.awaitTermination(5, TimeUnit.SECONDS);

        assertThat(runnableCount).isEqualTo(3);

    }

    @Test
    public void testDiscardOldestPolicy() throws InterruptedException {
        ExecutorService service = new ThreadPoolExecutor(
                1,1, 0, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(1),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardOldestPolicy());

        service.submit(runnable);  // handle by core thread
        service.submit(runnable);  // waiting in working queue

        service.submit(runnable1);   // no core thread, no place in working queue
                                     // discard task in working queue
        // run in main thread
        Thread.sleep(2_000);
        service.shutdown();
        service.awaitTermination(5, TimeUnit.SECONDS);

        assertThat(runnableCount).isEqualTo(1);
        assertThat(runnable1Count).isEqualTo(1);
    }

    @Test
    public void testDiscardPolicy() throws InterruptedException {
        ExecutorService service = new ThreadPoolExecutor(
                1,1, 0, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());

        service.submit(runnable);  // handle by core thread
        service.submit(runnable);  // waiting in working queue

        service.submit(runnable1);   // no core thread, no place in working queue
                                     // discard runable 1
        // run in main thread
        Thread.sleep(2_000);
        service.shutdown();
        service.awaitTermination(5, TimeUnit.SECONDS);

        assertThat(runnableCount).isEqualTo(2);
        assertThat(runnable1Count).isEqualTo(0);
    }
}
