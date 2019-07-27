package com.ryan.java.demo.concurrent.atomic;

// import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * 自旋锁的不足：
 *  仅适用于占用时间短、颗粒度很小的情景。
 *  需要硬件级别的原子操作。
 *  它无法保证公平性。
 *  每次读写操作需要同步每个处理器的缓存。
 *
 * CLH锁：
 *  鉴于自旋锁的不足，Craig,Landin, Hagersten发明了CLH锁，用来优化同步带来的花销。
 */
public class C203CASAndCLH {
}

/*
class CLHLock {

    private static Unsafe unsafe = null;
    private static final long valueOffset;
    private volatile CLHNode tail;

    class CLHNode {
        private boolean isLocked = true;
    }

    static {
        try {
            unsafe = getUnsafeInstance();
            valueOffset = unsafe.objectFieldOffset(CLHLock.class.getDeclaredField("tail"));
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    public void lock(CLHNode currentThreadNode) {
        CLHNode preNode = null;
        for (;;) {
            preNode = tail;
            if (unsafe.compareAndSwapObject(this, valueOffset, tail, currentThreadNode))
                break;
        }
        if (preNode != null)
            while (preNode.isLocked) {
            }
    }

    public void unlock(CLHNode currentThreadNode) {
        if (!unsafe.compareAndSwapObject(this, valueOffset, currentThreadNode, null))
            currentThreadNode.isLocked = false;
    }

    private static Unsafe getUnsafeInstance()
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field theUnsafeInstance = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafeInstance.setAccessible(true);
        return (Unsafe) theUnsafeInstance.get(Unsafe.class);
    }
}
*/