package com.ryan.java.demo.concurrent;

import org.junit.Test;

public class C009JvmExit {

    /**
     * JVM exist when:
     * 1. the last non-daemon thread exit
     *  daemon thread will exit automatically
     * 2. System.exit is invoked
     * 3. SIGINT received
     * 4. Force way: SIGKILL or Runtime.halt()
     *
     * shutdown hook
     */

    Runnable fooRunnable = new Runnable() {
        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() +
                    ": JVM gone!");
        }
    };

    // you can add more than one hook
    @Test
    public void shutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(fooRunnable));
        Runtime.getRuntime().addShutdownHook(new Thread(fooRunnable));
        Runtime.getRuntime().addShutdownHook(new Thread(fooRunnable));
    }

    @Test
    public void shutdownHook1() {
        Thread thread = new Thread(() -> {
            try {
                System.out.println("non-daemon begin sleep ...");
                Thread.sleep(5_000);
                System.out.println("non-daemon after sleep!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.setDaemon(false);
        thread.start();

        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> System.out.println("JVM gone!")));

        // fixme: JVM does not wait the non-daemon thread
    }

    /**
     * If there is only daemon thread, JVM will exit
     * When JVM exit, damon thread will be discard directly
     * fixme: damon thread only used for housekeeping, like clean memory ...
     */
    @Test
    public void daemon() {
    }
}
