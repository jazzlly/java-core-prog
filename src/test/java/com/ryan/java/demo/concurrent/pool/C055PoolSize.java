package com.ryan.java.demo.concurrent.pool;

public class C055PoolSize {
    /**
     * 线程池大小的估算：
     *  1. 对于计算型任务
     *      约等于 cpu number + 1
     *
     *  2. IO或阻塞类任务
     *      需要估算一个 计算时间和等待时间的比例
     *
     *      W = wait time
     *      C = compute time
     *      numOfThread = numberOfCpu*targetCpuUsage*(1+W/C)
     */
}
