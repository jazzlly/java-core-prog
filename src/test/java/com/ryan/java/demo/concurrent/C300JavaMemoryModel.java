package com.ryan.java.demo.concurrent;

import org.junit.Test;

public class C300JavaMemoryModel {

    /**
     *  想象时间轴，正方两个方向说
     *
     * 概念：
     *  memory barriers or fences
     *  获取内存协调的保障
     *
     *  程序的执行：
     *      顺序化一致性模型
     *          cpu执行指令的顺序，是程序中指令的出现顺序
     *         变量的每次读操作，都能够获取变量最新的写入值
     *
     *         仅仅是理论中的模型
     *
     *  重排序：reordering
     */

    /**
     * JMM: 概念
     *  动作 actions
     *      变量的读写，监视器的加锁，解锁，线程的start和join
     *
     *  偏序关系 or 顺序关系？
     *      happens-before
     *          想要保证执行动作B的线程，看到动作A的结果，
     *              （不论A和B是否发生在同一个线程中），
     *              A和B之间就必须满足happens-before关系
     *
     *          如果两个动作不满足happens-before关系
     *              JVM可以对它们随意的重新排序
     *
     *  happens-before规则：
     *
     *       volatile变量法则：
     *          对于volatile变量的写入操作，happens-before于每一个后续对该变量的读操作
     *          fixme: 同一线程中，volatile变量的操作顺序不会被重排序
     *              而且volatile变量写入后，会被其他线程立即看到
     *
     *       监视器锁法则：
     *          对一个监视器的解锁happens-before于对后续每一个对这个监视器的加锁
     *              线程A解锁前的事情，对于其他所有线程加锁后都是可见的
     *          fixme：同一个线程中，加锁和解锁的顺序不能被重排序
     *              监视器加锁，解锁动作，会被其他线程立即看到
     *
     *       线程启动法则：
     *          在一个线程里，对线程start()的调用happens-before于被启动线程中的所有动作
     *          fixme: Thread.start()操作和Thread.isAlived/join/interrupt操作不会被重排序
     *              start的原子性：不会调用了一半，线程就已经启动了
     *
     *       线程终结法则：
     *          线程中的任何动作，都happens-before于其他线程检测到这个线程已经终结，
     *              或从Thread.join中调用成功
     *              或从Thread.isAlive返回false
     *          fixme: 如果其他线程调用线程A的Thread.join成功，则A线程中所有动作都已经结束
     *              线程结束的动作，会被其他线程立即看见？
     *
     *       线程中断法则：
     *          其他线程调用线程A的中断操作，happens-before线程A发现中断
     *              （抛出interrputedException或者调用isInterrupted）
     *          fixme: 中断操作，会被立即通知到所有线程的
     *
     *       终结法则：
     *          对象构造函数的结束，happens-before它finalizer的开始
     *
     *       传递性：
     *          如果A happens-before B, B happens-before C，则A happens-before C
     *
     *      程序次序法则：
     *          如果程序中，动作B都在动作A的后面，则同一线程中动作A happens-before动作B
     *          fixme: 仅仅是个前提条件？这个法则和后面法则是and的关系？
     *              如果是充要条件，则就是一个线程内，完全不进行重排序了
     *
     *
     *
     *
     */

    /**
     * Thread A
     * 1 y = 1
     * 2 lock M
     * 3 x = 1                              Thread B
     * ...         M解锁前发生的每件事情
     * 10 unlock M                          3 lock M (wait ...)
     *             对于M加锁后，都变成可见
     * 11  ...    --schedule->              11 lock M (get)
     *                                      12 i = x
     *                                      13 unlock M
     *                                      14 j = y
     *
     * 每个线程中，lock/unlock前后的指令不能被重新排序
     */

    @Test
    public void threadRules() throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("child thread start!");
                while (true) {
                    try {
                        System.out.println("child thread loop ...");

                        Thread.sleep(5_000);
                    } catch (InterruptedException e) {
                        System.out.println("child thread is interrupted!");
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                System.out.println("child thread done!");
            }
        });

        thread.start();

        System.out.println("main thread invoked child.start()!");
        System.out.println("child is alive: " + thread.isAlive());

        Thread.sleep(2_000L);
        System.out.println("child interrupted begin:");
        thread.interrupt();
        System.out.println("child interrupted done!");

        while (true) {
            if (thread.isInterrupted() || !thread.isAlive()) {
                System.out.println("main detect child interrupted or die!");
                break;
            } else {
                // System.out.println("main detect child not interrupted!");
                Thread.sleep(100L);
            }
        }

        System.out.println("child begin join...");
        thread.join();
        System.out.println("child joined!");
    }
}

/**
 * Insufficiently synchronized program that can have surprising results
 */
class PossibleReordering {
    static int x = 0, y = 0;
    static int a = 0, b = 0;

    public static void abc(String[] args) throws InterruptedException {
        Thread one = new Thread(new Runnable() {
            public void run() {
                // fixme:下面两条指令可能会重新排序
                a = 1;
                x = b;
            }
        });
        Thread other = new Thread(new Runnable() {
            public void run() {
                // fixme:下面两条指令可能会重新排序
                b = 1;
                y = a;
            }
        });
        one.start();
        other.start();
        one.join();
        other.join();
        System.out.println("( " + x + "," + y + ")");
    }
}