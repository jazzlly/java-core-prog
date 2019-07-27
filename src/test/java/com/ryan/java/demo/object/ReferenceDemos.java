package com.ryan.java.demo.object;

import org.junit.Test;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class ReferenceDemos {

    @Test
    public void strongRef() {
        //Strong Reference - by default
        String abc = "foo";

        //Now, object to which 'g' was pointing earlier is
        //eligible for garbage collection.
        abc = null;
    }

    @Test
    public void softRef() {
        /**
         * 如果一个对象只有软引用指向它，垃圾回收时，
         * 只有内存不足时，垃圾回收器会立即回收该对象
         *
         * 适合作为缓存来使用
         * 可以指向数据库连接池中的空闲连接
         */
        Date date = new Date();
        SoftReference<Date> softReference = new SoftReference<>(date);

        date = null;

        // System.out.println(softReference.get());
        assertThat(softReference.get()).isNotNull();

        System.gc();
        System.runFinalization();

        // System.out.println(softReference.get());
        // 只有在内存不足时，gc才会回收软引用指向的对象
        assertThat(softReference.get()).isNotNull();
    }

    @Test
    public void weakRef() {
        /**
         * 如果一个对象只有弱引用指向它，当发生垃圾回收时，
         * 垃圾回收器会立即回收该对象
         *
         * Android 中的Glide 图片加载框架的内存缓存就使用到了弱引用缓存机制
         *
         * 常见的用途：
         *  通过一个hashmap跟踪一组widget
         *  如果没有及时删除widget, 就会导致内存泄漏
         *  或有可能上下文中没有从hashmap中删除widget的入口
         *  可以使用WeakHashMap来引用widget
         *
         * 一个使用弱引用的典型例子是WeakHashMap，
         * 它是除HashMap和TreeMap之外，Map接口的另一种实现。
         * WeakHashMap有一个特点：map中的键值(keys)都被封装成弱引用，
         * 也就是说一旦强引用被删除，WeakHashMap内部的弱引用就无法阻止该对象被垃圾回收器回收。
         *
         */

        // If the object only has weak refenceces, not strong or soft,
        // the object will be marked for garbage collection
        Date date = new Date();
        WeakReference<Date> weakReference = new WeakReference<>(date);

        date = null;
        // 这时，weakRef就可以被垃圾回收了

        System.out.println(weakReference.get());
        assertThat(weakReference.get()).isNotNull();

        System.gc();
        System.runFinalization();

        System.out.println(weakReference.get());
        assertThat(weakReference.get()).isNull();
        // WeakReference is used in WeakHashMap to reference entry object

        /**
         * 引用队列的作用：
         * 一旦弱引用对象开始返回null，该弱引用指向的对象就被标记成了垃圾。
         * 而这个弱引用对象（非其指向的对象）就没有什么用了。通常这时候需要进行一些清理工作。
         * 比如WeakHashMap会在这时候移除没用的条目来避免保存无限制增长的没有意义的弱引用。
         *
         * 引用队列可以很容易地实现跟踪不需要的引用。当你在构造WeakReference时传入一个ReferenceQueue对象，
         * 当该引用指向的对象被标记为垃圾的时候，这个引用对象会自动地加入到引用队列里面。
         * 接下来，你就可以在固定的周期，处理传入的引用队列，比如做一些清理工作来处理这些没有用的引用对象。
         */
    }


    @Test
    public void phantomRef() {
        /**
         * 虚引用基本等于没有引用
         *
         * 不会影响垃圾回收
         * 通过虚引用也不能获得对象的强引用
         *
         * 虚引用仅仅用于跟踪对象的垃圾回收状态，必须和reference queue一起用
         * 当虚引用指向的对象被垃圾回收后，虚引用就被gc加入到了qeue中
         *
         * 3. 虚引用：在静态内部类中，经常会使用虚引用。例如，一个类发送网络请求，承担callback的静态内部类，
         * 则常以虚引用的方式来保存外部类(宿主类)的引用，当外部类需要被JVM回收时，不会因为网络请求没有及时回来，
         * 导致外部类不能被回收，引起内存泄漏
         */

        String str = new String("foo");
        ReferenceQueue referenceQueue = new ReferenceQueue();
        PhantomReference phantomReference = new PhantomReference(str, referenceQueue);

        assertThat(referenceQueue.poll()).isNull();
        assertThat(phantomReference.get()).isNull();

        str = null;

        //取出虚引用所引用的对象，并不能通过虚引用访问被引用的对象，
        //所以此处输出的应该是null
        assertThat(phantomReference.get()).isNull();
        assertThat(referenceQueue.poll()).isNull();

        //强制进行垃圾回收
        System.gc();
        System.runFinalization();

        //取出引用队列最先进入队列中的引用于phantomReference进行比较
        assertThat(referenceQueue.poll()).isEqualTo(phantomReference);
    }
}
