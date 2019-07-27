package com.ryan.java.demo.concurrent.pool;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.*;


@Slf4j
public class C51ThreadPoolExecutorDistilled {

    /**
     * 线程池的适用场景：
     * 1. 同类的，独立的任务
     *  Web server， mail server, file server
     *
     *  应当避免将相互有依赖的任务提交到同一个线程池
     */
    @Test
    public void policy() {
    }

    /**
     * 核心池大小
     *  目标的线程数，一般在idle情况也不会销毁核心线程
     *      除非设置了allowCoreThreadTimeout
     *
     *  线程池创建后，不会创建所有的核心线程，需要等待有任务提交
     *      除非设置了prestartAllCoreThreads
     *
     *  工作队列满之前，也不会多创建线程
     *
     * 最大池大小
     *  最大池中的线程在idle时会被回收
     *
     * 最大池存活时间
     *
     *
     *
     */
    @Test
    public void concept() {
    }

    /**
     * Executor的线程共享策略是：
     * 0. 核心的线程会尽量重用
     * 1. 忙时创建新的线程
     * 2. 空闲时回收非核心的线程
     * 3. 线程发生异常时，创建一个新的线程取代出错的
     *
     * ThreadLocal和线程池一起使用时：
     *  注意线程退出时，ThreadLocal的清理
     *      threadLocal.remove()
     *          or
     *      threadLocal = null;
     */
    @Test
    public void threadSharePolicy() {
    }

    @Test
    public void workQueueNote() {
        /**
         * public ThreadPoolExecutor(
         *      int corePoolSize: 核心线程数, 即使idle了，也不会回收。
         *                          除非设置了allowCoreThreadTimeOut
         *      int maximumPoolSize: 最大线程数，当核心线程都分配时，会创建更多线程
         *      long keepAliveTime: 非核心线程的keepalive时间
         *      TimeUnit unit:
         *      BlockingQueue<Runnable> workQueue: 工作队列，保存还未分配给工作线程的Runnable
         *      ThreadFactory threadFactory: 一般使用默认的线程工厂
         *                                  线程工厂一般会设置一些线程通用的属性
         *                                         如名称前缀，daemonized等等
         *      RejectedExecutionHandler handler:
         *              当线程池满，工作队列满时，执行的拒绝策略
         *              默认是abort policy
         *      )
         */
        new ThreadPoolExecutor(
                1, 2, 60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(20),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
    }

    @Test
    public void workingQueueNote() {
        /**
         * workQueue: 已经提交但未执行的任务
         * 1. 直接提交的队列。由SynchronousQueue实现
         *  没有容量，每个插入都要等待一个删除，每个删除要等待一个插入
         *  总是将任务直接提交给线程执行，如果没有空闲线程，则创建新的线程
         *  如果线程数量达到最大值，则执行拒绝策略
         *  通常要设置非常大的maxPoolSize
         *
         *  Cached线程池使用的是直接提交队列
         *
         *  如果任务之间有相互依赖，需要相互等待，则可考虑使用
         *      cachedThreadPool。这样不会导致线程饥饿死锁
         **/
        Executors.newCachedThreadPool(); // ==
        new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());

        /**
         * 2. 有界任务队列。ArrayBlockingQueue实现
         * 如果核心线程满，则将任务提交到队列等待。
         * 如果队列也满，则创建更多线程
         * 如果线程数超过maxPoolSize，则执行拒绝策略
         *
         * 好的设计：
         * 1. 使用有界的队列，可以避免OOM
         * 2. Service层捕获RejectExecutionException
         *  并抛出含义明确的业务异常
         * 3. Controller层最终给客户端服务限流的响应
         *  客户端可以过一段时间进行重试
         */
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                10, 20, 60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10_000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
        try {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println("do something ...");
                    try {
                        Thread.sleep(10_000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (RejectedExecutionException e) {
            log.warn("Thread pool of foo service reject request!", e);
            throw new ServiceThrottleException(e);
        }

        /**
         * 3. 无界任务队列。LinkedBlockingQueue实现
         * 如果核心线程满，则将任务放入队列
         *
         * newSingleThreadExecutor和newFixedThreadPool
         *  都是无界队列
         *
         *  fixme: 无界队列会导致OOM
         **/
        Executors.newSingleThreadExecutor();    // ==
        new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());

        Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors()); // ==
        new ThreadPoolExecutor(4, 4,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());

        /**
         * 4. 优先任务队列。PriorityBlockingQueue实现
         * 特殊的无界任务队列
         *
         */

    }


    /**
     * 可定制的ThreadPoolExecutor
     */
    @Test
    public void customize() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                1, 1, 0, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1000));
        executor.setCorePoolSize(4);
        executor.setMaximumPoolSize(8);
        executor.setKeepAliveTime(60, TimeUnit.SECONDS);
        executor.setRejectedExecutionHandler(
                new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadFactory(r -> new Thread(r));
    }
}

class ServiceThrottleException extends RuntimeException {
    public ServiceThrottleException(Throwable cause) {
        super("Service foo throttling, should return 509 to client", cause);
    }
}

/**
 * 扩展Executor
 */
class MyExecutor extends ThreadPoolExecutor {
    public MyExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
    }

    @Override
    protected void terminated() {
        super.terminated();
    }
}
