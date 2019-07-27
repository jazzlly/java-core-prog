package com.ryan.java.demo.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Thread：两个方法
 *  run()
 *  start()
 */
@Slf4j
public class C001Thread {

    @Test
    public void smoke() throws InterruptedException {
        Thread thread = new DemoThread();
        thread.start();

        thread.join();
    }

    @Test
    public void interrupted() throws InterruptedException {
        Thread thread = new DemoThread();
        thread.start();

        Thread.sleep(1_000);
        thread.interrupt();
        thread.join();
    }
}

class DemoThread extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.println(Thread.currentThread().getName() +
                    ": output ...");
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                 e.printStackTrace();
                //  log.info("");
                System.out.println("isInterrupted: " + isInterrupted());
                System.out.println("isInterrupted: " + isInterrupted());
                System.out.println("interrupted: " + interrupted());
                System.out.println("interrupted: " + interrupted());
            }
        }
    }
}
