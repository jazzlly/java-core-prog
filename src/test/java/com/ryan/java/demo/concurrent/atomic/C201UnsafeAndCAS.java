package com.ryan.java.demo.concurrent.atomic;

import lombok.Getter;
import org.junit.Test;
// import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * https://juejin.im/post/5bbfeace6fb9a05d1117a644
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
public  class C201UnsafeAndCAS {

}

    /**
     * 调用它的类是否由启动类加载器Bootstrap ClassLoader（它的类加载器为null）加载
     * 否则就抛出SecurityException
     */
    /*
    @Test(expected = SecurityException.class)
    public  void tryGetUnsafe() {
        Unsafe unsafe = Unsafe.getUnsafe();
    }
    */

    /**
     * 可以通过反射获取到Unsafe
     */
    /*
    @Test
    public void getUnsafeByReflection() throws NoSuchFieldException, IllegalAccessException {
        Unsafe unsafe = getUnsafe();

        long offset= unsafe.objectFieldOffset(UnsafeTest.class.getDeclaredField("flag"));
        int expect = 100;
        int update = 101;

        UnsafeTest unsafeTest = new UnsafeTest();
        System.out.println("unsafeTest对象的flag字段的地址偏移量为："+offset);
        System.out.println("CAS操作前的flag值为：" +unsafeTest.getFlag());
        unsafe.compareAndSwapInt(unsafeTest, offset, expect, update);
        System.out.println("CAS操作后的flag值为：" +unsafeTest.getFlag());
    }

    static Unsafe getUnsafe() throws IllegalAccessException, NoSuchFieldException {
        Field unsafe = Unsafe.class.getDeclaredField("theUnsafe");
        unsafe.setAccessible(true);
        return  (Unsafe) unsafe.get(Unsafe.class);
    }
}*/

    /*
@Getter
class UnsafeTest {
    private int flag = 100;
}
*/


