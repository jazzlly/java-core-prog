package com.ryan.java.demo.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class C86SystemNanoTime {

    static long nanoForOneSecond = 1_000_000_000L;

    @Test
    public void nanoTime() throws InterruptedException {
        long before = System.nanoTime();
        System.out.println(before);
        Thread.sleep(1_000);
        System.out.println(System.nanoTime() - before);

        long nanoForOneSecond = 1_001_442_708L;
    }

    @Test
    public void spinOneSecond() {
        long deadline = System.nanoTime() + nanoForOneSecond;
        log.info("Spin begin ...");
        while (true) {
            if (System.nanoTime() < deadline) {
                Thread.yield();
            } else {
                log.info("Spin Timeout!");
                break;
            }
        }
    }
}
