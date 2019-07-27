package com.ryan.java.demo.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.*;

/**
 * 底层是通过LockSupport.park()/unpark()来实现等待的
 */
@Slf4j
public class C85SynchronousQueue {

    private static SynchronousQueue<String> synchronousQueue =
            new SynchronousQueue<>();

    static Runnable producer = () -> {
        while (true) {
            try {
                log.info("producer put string ...");
                synchronousQueue.put(UUID.randomUUID().toString());
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                log.info("Producer was interrupted", e);
                break;
            }
        }
    };

    static Runnable consumer = () -> {
        while (true) {
            try {
                String s = synchronousQueue.take();
                log.info("Consumer got string: " + s);
            } catch (InterruptedException e) {
                log.info("Consumer was interrupted", e);
                break;
            }
        }
    };


    @Test
    public void smoke() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(2);
        Future futureProducer = service.submit(producer);
        Future futureConsumer = service.submit(consumer);

        Thread.sleep(5_000);
        futureProducer.cancel(true);
        futureConsumer.cancel(true);

        service.shutdown();
        service.awaitTermination(5, TimeUnit.SECONDS);
    }
}
