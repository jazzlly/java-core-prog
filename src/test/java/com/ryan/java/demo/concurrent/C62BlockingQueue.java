package com.ryan.java.demo.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.*;

/**
 * blocking operation:
 *  put() / take()
 *      can be interrupted
 * no blocking operation:
 *  offer() / poll(timeout)
 *
 *  implemented by ReentrantLock and two Condition
 *
 *  有界队列是一种安全的方式
 *      错误策略是当超出限制时，返回错误，或block生产者线程
 *
 *
 */
@Slf4j
public class C62BlockingQueue {
    BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(5);

    class Producer implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                try {
                    blockingQueue.put("foo" + i);
                    System.out.println("producer: foo" + i);
                } catch (InterruptedException e) {
                    // e.printStackTrace();
                    log.info("");
                    System.out.println("producer interrupted!");
                    break;
                }
            }
        }


    }

    class Consumer implements Runnable {

        @Override
        public void run() {
            while (true) {

                try {
                    String s =  blockingQueue.take();
                    if (s != null) {
                        log.info("");
                        System.out.println("got string: " + s);
                    }
                    Thread.sleep(1_000);

                } catch (InterruptedException e) {
                    // e.printStackTrace();
                    log.info("");
                    System.out.println("consumer interrupted");
                    break;
                }

            }
        }
    }

    @Test
    public void smoke() throws InterruptedException, ExecutionException {
        ExecutorService service = Executors.newScheduledThreadPool(2);
        Future future = service.submit(new Producer());
        Future future1 = service.submit(new Consumer());

        Thread.sleep(10_000);

        future.cancel(true);
        future1.cancel(true);

        service.shutdown();
        service.awaitTermination(10, TimeUnit.SECONDS);
    }
}
