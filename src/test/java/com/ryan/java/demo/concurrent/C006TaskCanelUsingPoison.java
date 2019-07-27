package com.ryan.java.demo.concurrent;

import org.junit.Test;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class C006TaskCanelUsingPoison {

    static final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    public static final String POISON = "go peace";

    class FooConsmer implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    String msg = queue.take();
                    if (msg == POISON) {
                        System.out.println("consumer got poison!");
                        System.out.println("consumer task end!");
                        break;
                    }
                    System.out.println("consumer got msg: " + msg);
                } catch (InterruptedException e) {
                    System.out.println("consumer interrupted!");
                    // ignore
                } finally { }
            }
        }
    }

    class FooProducer implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1_000);
                    queue.put(UUID.randomUUID().toString());
                } catch (InterruptedException e) {
                    // e.printStackTrace();
                    System.out.println("producer interrupted");
                    while (true) {
                        try {
                            System.out.println("producer sent poison!");
                            queue.put(POISON);
                            break;
                        } catch (InterruptedException e1) {
                            // ignore
                        } finally {}
                    }
                    break;
                } finally {}
            }
        }
    }

    @Test
    public void smoke() throws InterruptedException {
        Thread producer = new Thread(new FooProducer(), "msg producer");
        Thread consumer = new Thread(new FooConsmer(), "msg consumer");

        producer.start();
        consumer.start();

        Thread.sleep(5_000);
        producer.interrupt();

        producer.join();
        consumer.join();
    }
}


