package com.ryan.java.demo.concurrent.lock;

import net.jcip.annotations.GuardedBy;
import org.junit.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * 两个哲学家吃饭，每人必须拿起左右两个叉子才能吃
 * A先拿左边的叉子，再拿右边的叉子
 * B先拿右边的叉子，再拿边的叉子
 *
 *
 * 如何检测deadlock:
 *   1. 看现象:表面现象相关线程不工作，cpu占用率为0
 *   2. 使用工具:
 *   jps -l ：获取进程号
 *   jstack pid : 打印进程的堆栈
 *      好的习惯：给线程创建一个有意义的名称，这样在堆栈里面看到的线程名
 *      就是"philosopher1", "philosopher2"
 *      而不是"Thread-1", "Thread-2"
 *   kill -3 ：dump jvm
 *      SIGQUIT
 *
 *  死锁的pattern
 *    1. 锁顺序死锁： 两个线程使用不同的顺序获取几个锁
 *      如果所有线程获取锁的顺序相同，就不会出现死锁
 *
 *    2. 动态的锁顺序死锁
 *
 *  持有锁的时候调用外部方法
 *    解决方法是：
 *    在方法内部使用锁保护内部的资源
 *    将外部方法放到锁的外面
 *
 *  监测和避免死锁：
 *  1. 尝试使用Lock.tryLock替代内部锁
 *      trylock有超时机制，当发生超时时，就有死锁的可能性
 *
 */
public class C90DeadLockDemo {
    private static ReentrantLock leftFork =
            new ReentrantLock();
    private static ReentrantLock rightFork =
            new ReentrantLock();

    static Runnable philosopher1 = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    System.out.println("philosopher 1, before lock ...");
                    leftFork.lockInterruptibly();
                    System.out.println("philosopher 1, got left fork ...");

                    Thread.yield();         // go deadlock
                    Thread.sleep(500);

                    rightFork.lockInterruptibly();
                    System.out.println("philosopher 1, got right fork ...");

                    System.out.println("philosopher 1, after lock ...");
                    System.out.println("philosopher 1, begin eating ...");

                    Thread.sleep((long) (2000 * Math.random() + 500));
                    System.out.println("philosopher 1, done eating!");
                    rightFork.unlock();
                    leftFork.unlock();

                    System.out.println("philosopher 1, begin thinking ...");
                    Thread.sleep((long) (2000 * Math.random() + 500));
                    System.out.println("philosopher 1, done thinking!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("philosopher 1 interrupted!");
                    break;
                }
            }
        }
    };

    static Runnable philosopher2 = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    System.out.println("philosopher 2, before lock ...");
                    rightFork.lockInterruptibly();
                    System.out.println("philosopher 2, got right fork ...");
                    Thread.yield();  // go deadlock ...
                    Thread.sleep(500);

                    leftFork.lockInterruptibly();
                    System.out.println("philosopher 2, got left fork ...");

                    System.out.println("philosopher 2, after lock ...");
                    System.out.println("philosopher 2, begin eating ...");

                    Thread.sleep((long) (2000 * Math.random() + 500));
                    System.out.println("philosopher 2, done eating!");
                    leftFork.unlock();
                    rightFork.unlock();

                    System.out.println("philosopher 2, begin thinking ...");
                    Thread.sleep((long) (2000 * Math.random() + 500));
                    System.out.println("philosopher 2, done thinking!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("philosopher 2 interrupted!");
                    break;
                }
            }
        }
    };

    @Test
    public void smoke() throws InterruptedException {
        Thread thread1 = new Thread(philosopher1, "philosopher1");
        Thread thread2 = new Thread(philosopher2, "philosopher2");

        thread1.start();
        thread2.start();

        Thread.sleep(1000_000);

        thread1.interrupt();
        thread2.interrupt();

        thread1.join();
        thread2.join();
    }
}

/**
 * Dynamic lock-ordering deadlock
 */
class DynamicOrderDeadlock {
    // Warning: deadlock-prone!
    public static void transferMoney(Account fromAccount,
                                     Account toAccount,
                                     DollarAmount amount)
            throws InsufficientFundsException {
        synchronized (fromAccount) {
            synchronized (toAccount) {
                if (fromAccount.getBalance().compareTo(amount) < 0)
                    throw new InsufficientFundsException();
                else {
                    fromAccount.debit(amount);
                    toAccount.credit(amount);
                }
            }
        }
    }

    private static final Object tieLock = new Object();

    /**
     * 保证锁的顺序相同
     *  比较System.identityHashCode(object)
     *      这个值有可能相同。所以需要另外一个附加锁
     *
     *  如果有一个comparable的属性，就可以直接用这个属性
     *      作为锁的顺序
     */
    public void transferMoney1(final Account fromAcct,
                              final Account toAcct,
                              final DollarAmount amount)
            throws InsufficientFundsException {
        class Helper {
            public void transfer() throws InsufficientFundsException {
                if (fromAcct.getBalance().compareTo(amount) < 0)
                    throw new InsufficientFundsException();
                else {
                    fromAcct.debit(amount);
                    toAcct.credit(amount);
                }
            }
        }
        int fromHash = System.identityHashCode(fromAcct);
        int toHash = System.identityHashCode(toAcct);

        if (fromHash < toHash) {
            synchronized (fromAcct) {
                synchronized (toAcct) {
                    new Helper().transfer();
                }
            }
        } else if (fromHash > toHash) {
            synchronized (toAcct) {
                synchronized (fromAcct) {
                    new Helper().transfer();
                }
            }
        } else {
            synchronized (tieLock) {
                synchronized (fromAcct) {
                    synchronized (toAcct) {
                        new Helper().transfer();
                    }
                }
            }
        }
    }


    private static Random rnd = new Random();

    // 使用代用超时机制的tryLock
    public boolean transferMoneyWithLock(Account fromAcct,
                                 Account toAcct,
                                 DollarAmount amount,
                                 long timeout,
                                 TimeUnit unit)
            throws InsufficientFundsException, InterruptedException {
        long fixedDelay = 1;
        long randMod = 2;
        long stopTime = System.nanoTime() + unit.toNanos(timeout);

        while (true) {
            if (fromAcct.lock.tryLock()) {
                try {
                    if (toAcct.lock.tryLock()) {
                        try {
                            if (fromAcct.getBalance().compareTo(amount) < 0)
                                throw new InsufficientFundsException();
                            else {
                                fromAcct.debit(amount);
                                toAcct.credit(amount);
                                return true;
                            }
                        } finally {
                            toAcct.lock.unlock();
                        }
                    }
                } finally {
                    fromAcct.lock.unlock();
                }
            }
            if (System.nanoTime() < stopTime)
                return false;
            NANOSECONDS.sleep(fixedDelay + rnd.nextLong() % randMod);
        }
    }
    static class DollarAmount implements Comparable<DollarAmount> {
        // Needs implementation
        public DollarAmount(int amount) {
        }
        public DollarAmount add(DollarAmount d) {
            return null;
        }
        public DollarAmount subtract(DollarAmount d) {
            return null;
        }
        public int compareTo(DollarAmount dollarAmount) {
            return 0;
        }
    }

    static class Account {
        public Lock lock;
        private DollarAmount balance;
        private final int acctNo;
        private static final AtomicInteger sequence = new AtomicInteger();

        public Account() {
            acctNo = sequence.incrementAndGet();
        }

        void debit(DollarAmount d) {
            balance = balance.subtract(d);
        }

        void credit(DollarAmount d) {
            balance = balance.add(d);
        }

        DollarAmount getBalance() {
            return balance;
        }

        int getAcctNo() {
            return acctNo;
        }
    }
    static class InsufficientFundsException extends Exception {
    }
}

/**
 * Lock-ordering deadlock between cooperating objects
 *
 * 持有锁的使用，调用外部方法是危险的！
 */
class CooperatingDeadlock {
    interface Point {}

    // Warning: deadlock-prone!
    class Taxi {
        @GuardedBy("this") private Point location, destination;
        private final Dispatcher dispatcher;

        public Taxi(Dispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

        public synchronized Point getLocation() {
            return location;
        }

        // fixme: 持有锁的时候调用外部方法是危险的
        public synchronized void setLocation(Point location) {
            this.location = location;
            if (location.equals(destination))
                dispatcher.notifyAvailable(this);
        }

        // Good!
        public void setLocationGood(Point location) {
            boolean dispatch = false;
            synchronized (this) {
                this.location = location;
                dispatch = location.equals(destination);
            }

            if (dispatch) {
                dispatcher.notifyAvailable(this);
            }
        }

        public synchronized Point getDestination() {
            return destination;
        }

        public synchronized void setDestination(Point destination) {
            this.destination = destination;
        }
    }

    class Dispatcher {
        @GuardedBy("this") private final Set<Taxi> taxis;
        @GuardedBy("this") private final Set<Taxi> availableTaxis;

        public Dispatcher() {
            taxis = new HashSet<Taxi>();
            availableTaxis = new HashSet<Taxi>();
        }

        public synchronized void notifyAvailable(Taxi taxi) {
            availableTaxis.add(taxi);
        }

        // fixme: deadlock happens
        public synchronized Image getImage() {
            Image image = new Image();
            for (Taxi t : taxis)
                image.drawMarker(t.getLocation());
            return image;
        }

        // 使用了原有set的一个拷贝
        public Image getImageGood() {
            Set<Taxi> taxis;
            synchronized (this) {
                taxis = new HashSet<>(this.taxis);
            }

            Image image = new Image();
            for (Taxi t : taxis)
                image.drawMarker(t.getLocation());
            return image;
        }

    }

    class Image {
        public void drawMarker(Point p) {
        }
    }
}

