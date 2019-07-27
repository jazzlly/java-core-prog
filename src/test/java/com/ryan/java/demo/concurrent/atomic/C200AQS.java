package com.ryan.java.demo.concurrent.atomic;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * AQS 是ReentrantLock, Semaphore, CountdownLatch, 等等的底层实现
 *
 * 两种基本操作：
 *  1. acquire
 *  2. release
 */
public class C200AQS {
    AbstractQueuedSynchronizer abstractQueuedSynchronizer;
}
