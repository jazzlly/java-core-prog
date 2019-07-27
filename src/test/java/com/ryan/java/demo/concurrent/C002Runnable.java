package com.ryan.java.demo.concurrent;

/**
 * fixme:
 * Runnable can NOT return value
 * Runnable can NOT thrown checked exception
 *
 *  使用Callable是一个好的习惯
 *      Callable::call()返回值
 *      Callable::call()抛出异常
 */
public class C002Runnable implements Runnable {
    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println("Thread 1 output ...");

            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        C002Runnable demo = new C002Runnable();
        Thread thread = new Thread(demo);
        thread.start();

        System.out.println("begin join ...");
        thread.join();
        System.out.println("after join!");
    }
}

/**
 * 1、采用实现Runnable、Callable接口的方式创建多线程时，
 *      线程类只是实现了Runnable接口或Callable接口，还可以继承其他类。
 *      缺点是编程稍微复杂，如果要访问当前线程，则必须使用Thread.currentThread()方法。
 *
 * 2、使用继承Thread类的方式创建多线程时，编写简单，如果需要访问当前线程，
 *      则无需使用Thread.currentThread()方法，直接使用this即可获得当前线程。
 *      缺点是线程类已经继承了Thread类，所以不能再继承其他父类。
 *
 * 3、Runnable和Callable的区别
 * (1) Callable规定重写call()，Runnable重写run()。
 * (2) Callable的任务执行后可返回值，而Runnable的任务是不能返回值的。
 * (3) call方法可以抛出异常，run方法不可以。
 * (4) 运行Callable任务可以拿到一个Future对象，表示异步计算的结果。
 *      它提供了检查计算是否完成的方法，以等待计算的完成，并检索计算的结果。
 *      通过Future对象可以了解任务执行情况，可取消任务的执行，还可获取执行结果。
 *
 * 链接：https://juejin.im/post/5ba133126fb9a05ce02a6f12
 */
