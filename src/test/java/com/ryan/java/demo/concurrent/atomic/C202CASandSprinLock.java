package com.ryan.java.demo.concurrent.atomic;

// import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * JDK并发AQS系列(三)
 * https://juejin.im/post/5bc7d3d3f265da0aa41e9d4f
 * 掘金：超人汪小建
 */
/*
同步器一般用acquire和release方法执行获取释放锁操作，
    acquire方法包括的逻辑是先尝试获取锁，成功则往下执行，否则把线程放到等待队列中并可能将线程阻塞；
    release方法包含的逻辑是释放锁，唤醒等待队列中一个或多个线程去尝试获取锁。看看在AQS中锁的获取与释放。

锁的获取逻辑 acquire():
if(尝试获取锁失败) {
    创建node
    使用CAS方式把node插入到队列尾部
    while(true){
        if(尝试获取锁成功 并且 node的前驱节点为头节点){
            把当前节点设置为头节点
            跳出循环
        }else{
            使用CAS方式修改node前驱节点的waitStatus标识为signal
            if(修改成功)
                挂起当前线程
        }
    }
}

锁的释放逻辑 release():
if(尝试释放锁成功){
    唤醒后续节点包含的线程
}
*/
public class C202CASandSprinLock {
}


/**
 * 自旋锁的不足：
 *  仅适用于占用时间短、颗粒度很小的情景。
 *  需要硬件级别的原子操作。
 *  它无法保证公平性。
 *  每次读写操作需要同步每个处理器的缓存。
 */
/*
class SpinLock {
    private static Unsafe unsafe = null;
    private static final long valueOffset;
    private volatile int value = 0;
    static {
        try {
            unsafe=getUnsafeInstance();
            valueOffset = unsafe.objectFieldOffset(SpinLock.class
                    .getDeclaredField("value"));
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    private static Unsafe getUnsafeInstance() throws SecurityException,
            NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException {
        Field theUnsafeInstance = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafeInstance.setAccessible(true);
        return (Unsafe) theUnsafeInstance.get(Unsafe.class);
    }

    public void lock() {
        for (;;) {
            int newV = value + 1;
            if(newV==1)
                if (unsafe.compareAndSwapInt(this, valueOffset, 0, newV))
                    return ;
        }
    }
    public void unlock() {
        unsafe.compareAndSwapInt(this, valueOffset, 1, 0);
    }
}
*/


