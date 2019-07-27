package com.ryan.java.demo.concurrent;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchDemo {

    CountDownLatch latch = new CountDownLatch(1);

    public void shutdown() throws InterruptedException {
        latch.countDown();
        latch.await();
    }

    public static void main(String[] args) throws InterruptedException {
        CountDownLatchDemo demo = new CountDownLatchDemo();

        Thread thread = new Thread(() -> {
            while (demo.latch.getCount() == 1L) {
                System.out.println("Processing data ...");

                try {
                    Thread.sleep(2_000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Exiting working thread ...");
        });
        thread.start();

        Thread.sleep(10_000);
        demo.shutdown();
        thread.join();

        System.out.println("Main exiting ...");
    }
}
