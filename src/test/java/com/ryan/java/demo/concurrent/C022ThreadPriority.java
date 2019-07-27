package com.ryan.java.demo.concurrent;

public class C022ThreadPriority extends Thread {
    @Override
    public void run() {
        while (true) {
            System.out.println("Thread 1 output ...");

            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        C022ThreadPriority demo1 = new C022ThreadPriority();
        demo1.setDaemon(true);
        demo1.setPriority(Thread.MAX_PRIORITY);
        demo1.start();

        System.out.println("main thread sleeping ...");
        sleep(5_000);
        System.out.println("main thread exiting ...");

        // fixme: 当一个应用内，只有守护线程时，java虚拟机就会自动退出
    }
}
