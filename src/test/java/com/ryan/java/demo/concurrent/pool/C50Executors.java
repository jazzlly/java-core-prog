package com.ryan.java.demo.concurrent.pool;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class C50Executors {

    @Test
    public void smoke() {
        // Fixed thread pool: 如果没有空闲线程，则任务放到一个队列中等待
        // 如果线程出现异常，会补充一个线程到线程池
        ExecutorService service1 = Executors.newFixedThreadPool(4);

        // Cached thread pool:
        // 线程池的数量是可变的，如果没有空闲线程，则创建新的线程处理任务
        // 对线程池的长度没有限制
        ExecutorService service2 = Executors.newCachedThreadPool();

    }

    static Runnable runnable = () -> {
        log.info("");
        System.out.println(Thread.currentThread().getName() +
                ": begin ...");
        try {
            Thread.sleep(2_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() +
                ": end!");
    };

    // 每次两个线程并发执行，其余线程在队列中等待
    @Test
    public void fixedPool() throws InterruptedException {
        // 每次两个线程并发执行，其余线程在队列中等待
        ExecutorService service = Executors.newFixedThreadPool(2);
        for (int i = 0; i < 10; i++) {
            service.submit(runnable);
        }

        service.shutdown();
        service.awaitTermination(10, TimeUnit.SECONDS);
    }

    @Test
    public void lifeCycle() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(2);
        // 1. running status

        for (int i = 0; i < 10; i++) {
            service.submit(runnable);
        }

        service.shutdown();
        List<Runnable> runnables = service.shutdownNow();
        // 2. shutting down
        // stop accept new task, waiting old task to exit
        // 2.5 shutdownNow() force to cancel all running task
            // and task in queue
        // 2.6 在shutting down状态提交任务，会执行reject execution handler

        service.awaitTermination(10, TimeUnit.SECONDS);
        // or
        while (true) {
            if (service.isTerminated()) {
                break;
            }
            Thread.sleep(1_000);
        }

        // 3. terminated
    }

    // 同时启动多个线程
    @Test
    public void cachedPool() throws InterruptedException {
        ExecutorService service = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            service.submit(runnable);
        }

        service.shutdown();
        service.awaitTermination(10, TimeUnit.SECONDS);
    }

    @Test
    public void schedule() throws InterruptedException {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(2);
        service.schedule(runnable, 2, TimeUnit.SECONDS);

        log.info("");
        service.shutdown();
        service.awaitTermination(5, TimeUnit.SECONDS);
        log.info("");
    }

    @Test
    public void scheduleFixedRate() throws InterruptedException {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        service.scheduleAtFixedRate(runnable, 0, 3, TimeUnit.SECONDS);

        Thread.sleep(20_000);
        service.shutdown();
        service.awaitTermination(5, TimeUnit.SECONDS);
    }
}

/**
 * LifecycleWebServer
 * <p/>
 * Web server with shutdown support
 *
 * @author Brian Goetz and Tim Peierls
 */
@Slf4j
class LifecycleWebServer {
    private final ExecutorService exec = Executors.newCachedThreadPool();

    public void start() throws IOException {
        ServerSocket socket = new ServerSocket(80);
        while (!exec.isShutdown()) {
            try {
                final Socket conn = socket.accept();
                exec.execute(new Runnable() {
                    public void run() {
                        handleRequest(conn);
                    }
                });
            } catch (RejectedExecutionException e) {
                if (!exec.isShutdown())
                    log.info(   "task submission rejected", e);
            }
        }
    }

    public void stop() {
        exec.shutdown();
    }

    void handleRequest(Socket connection) {
        Request req = readRequest(connection);
        if (isShutdownRequest(req))
            stop();
        else
            dispatchRequest(req);
    }

    interface Request {
    }

    private Request readRequest(Socket s) {
        return null;
    }

    private void dispatchRequest(Request r) {
    }

    private boolean isShutdownRequest(Request r) {
        return false;
    }
}
