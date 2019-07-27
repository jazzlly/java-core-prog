package com.ryan.java.demo.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.*;


/**
 * 面试官：你知道如何发布或共享一个对象吗？
 */
public class C001PublishEscape {
}

/**
 * 变量逸出原有作用域
 */
class Main {
    private String[] strs = {"1", "2", "3"};

    public String[] getStrs() {
        return strs;
    }

    /**
     * 通过访问对象中的共有方法获取私有变量的值，然后更改内部数据，则导致变量逸出作用域。
     *
     * getter没毛病，不过对仅仅需要发布的对象进行发布
     */
    public static void main(String[] args) {
        Main m1 = new Main();
        System.out.println(Arrays.toString(m1.getStrs()));
        m1.getStrs()[0] = "4";
        System.out.println(Arrays.toString(m1.getStrs()));
    }
}

/**
 * 对象逸出：当一个对象还没构造完成，就使它被其他线程所见。
 */
class Main1 {

    public Main1() throws InterruptedException {
        System.out.println(Main1.this);
        System.out.println(Thread.currentThread());
        Thread t = new Thread(InnerClass::new);
        t.start();  // Main1的构造函数还没有完成，就在其他线程中看到了this

        Thread.sleep(1_000);
    }

    class InnerClass {
        public InnerClass() {
            System.out.println(Main1.this);
            System.out.println(Thread.currentThread());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new Main1();
    }
}

/**
 * 通过私有构造函数 + 工厂模式解决。
 */
class Main2 {
    private Thread t;

    private Main2() {
        System.out.println(Main2.this);
        System.out.println(Thread.currentThread());
        this.t = new Thread(InnerClass::new);
    }

    class InnerClass {
        public InnerClass() {
            System.out.println(Main2.this);
            System.out.println(Thread.currentThread());
        }
    }

    public static Main2 getMainInstance() {
        Main2 main = new Main2();
        main.t.start();
        return main;
    }

    public static void main(String[] args) {
        getMainInstance();
    }
}

/**
 * 安全发布策略:
 *  安全地发布对象是保证对象在其他线程可见之前一定是完成初始化的
 *
 *  那么我们要做的就是控制初始化过程，
 *  1. 首先就需要将构造器私有化，
 *  2. 接下来就通过不同的方式来完成对象初始化。
 */

/**
 * 直接在静态变量后new出来或者在static代码块中初始化，
 *
 *  通过JVM的单线程类加载机制来保证该对象在其他对象访问之前被初始化
 *  类加载是单线程的
 *
 */
@Slf4j
class Singleton1 {
    private static Singleton1 instance = new Singleton1();

    private Singleton1() {
        log.info("Singleton1 constructor!");
    }

    public static Singleton1 getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        log.info("Singleton1 main");
    }
}

/**
 * 将对象的初始化手动同步处理
 *  并发性差
 */
class Singleton2 {
    private static Singleton2 instance;

    private Singleton2() {
    }

    public static synchronized Singleton2 getInstance() {
        if (instance == null) {
            instance = new Singleton2();
        }
        return instance;
    }
}

/**
 * volatile + double null checking: nsn
 */
class Singleton3 {
    private static volatile Singleton3 instance;

    private Singleton3() {
    }

    public static Singleton3 getInstance() {
        if (instance == null) {
            synchronized (Singleton3.class) {
                if (instance == null) {
                    instance = new Singleton3();
                }
            }
        }
        return instance;
    }
}

/**
 * 静态初始化器由JVM在类的初始化阶段执行
 * 同步机制由JVM自身保证
 */
@Slf4j
class Singleton4Simple {
    private static class Holder {
        private static Singleton4Simple instance = new Singleton4Simple();
    }

    private Singleton4Simple() {
        log.info("Singleton4Simple constructor!");
    }

    public static Singleton4Simple getInstance() {
        return Holder.instance;
    }

    public static void main(String[] args) {
        // Singleton4Simple.getInstance();
        log.info("Singleton4Simple main!");
        // 不会自动加载内部类
    }
}

@Slf4j
class Singleton4 {
    public static final String FOO = "foo";
    static {
        log.info("Singleton4 外部类静态初始化");
    }

    private Singleton4() {
        log.info("Singleton4 constructor");
    }

    private static class Hodler {
        private static Singleton4 singleton4 = new Singleton4();
        static {
            log.info("Holder 内部类静态初始化");
        }
    }

    public static Singleton4 getInstance() {
        log.info("Singleton4 getInstance");
        log.info("Singleton4 外部类第一次引用到内部类Holder");
        return Hodler.singleton4;
    }

    public static void main(String[] args) throws InterruptedException {
        log.info(Singleton4.FOO);
        log.info("外部类静态初始化后，不会自动初始化内部类");

        Thread.sleep(5_000);

        log.info("");
        Singleton4.getInstance();
    }
}

/**
 * 安全共享策略
 *
 * 1. 局部变量
 *  需要确保局部变量不会逃逸
 *
 * 2. 不可变对象
 *  String, Boolean, Byte, Integer, ...
 *
 * 3. 线程安全对象
 *  Collections.SynchronizedList, HashTable, ConcurrentHashMap, ...
 *
 * 4. ThreadLocal对象
 *
 * 将对象封闭在一个线程内部，那么其他线程当然无法访问，则这些对象不可能涉及到共享问题，有以下方式：
 *
 *  局部变量封闭：局部变量的固有属性之一就是封闭在执行线程内，无法被外界引用，所以尽量使用局部变量可以减少逸出的发生
 *
 *  ThreadLocal：是一个能提供线程私有变量的工具类。基于每个Thread对象中保存了ThreadLocalMap对象，
 *      ThreadLocal类就在get和set方法中通过<ThreadLocal, value>键值对操作ThreadLocalMap，推荐。
 *      通常使用在传递每个线程（请求）的上下文。
 *
 */
class ThreadLocalTest {
    private ThreadLocal<String> localString = new ThreadLocal<>();

    public static void main(String[] args) {
        ThreadLocalTest t = new ThreadLocalTest();
        Runnable runnable = () -> {
            t.localString.set("localString in thread: " + Thread.currentThread());
            System.out.println(t.localString.get());
        };
        new Thread(runnable).start();
        new Thread(runnable).start();
    }
}

/**
 * 注意使用了Collections.unmodifiableMap
 */
@Slf4j
class ImmutableExample1 {

    private final static Integer a = 1;
    private final static String b = "2";
    private final static Map<Integer, Integer> map =
            Collections.unmodifiableMap(new HashMap<>());
        // 注意使用了Collections.unmodifiableMap

    static {
        map.put(1, 2);
        map.put(3, 4);
        map.put(5, 6);
    }

    public static void main(String[] args) {
//        a = 2;
//        b = "3";
//        map = Maps.newHashMap();
        // map.put(1, 3);
        log.info("{}", map.get(1));
    }
}
/**
 *  使用线程安全的类
 *  使用了如下容器类，可以安全的发布容器内的对象
 *
 * StringBuilder -> StringBuffer
 * SimpleDateFormat -> JodaTime
 * ArrayList -> Vector, Stack, CopyOnWriteArrayList
 *      值是安全发布的
 * HashSet -> Collections.synchronizedSet(new HashSet()), CopyOnWriteArraySet
 *      值是安全发布的
 * TreeSet -> Collections.synchronizedSortedSet(new TreeSet()), ConcurrentSkipListSet
 *  值是安全发布的
 * HashMap -> HashTable, ConcurrentHashMap, Collections.synchronizedMap(new HashMap())
 *  键和值都是安全发布的
 * TreeMap -> ConcurrentSkipListMap, Collections.synchronizedSortedMap(new TreeMap())
 *  键和值都是安全发布的
 */

/**
 * 如果一个类会被多个线程访问， 它就需要被正确发布？
 */

class DemoEscape {
    // 正常的发布？
    public static Set<String> stringSet;
    public void initilize() {
        stringSet = new HashSet<>();
    }

    /**
     * 下面函数式threadsafe的
     * 局部变量时线程安全的
     */
    public List<String> localIsOk() {
        // 注意，不要让strings逃逸出去
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            strings.add(UUID.randomUUID().toString());
        }

        // 逃逸了！
        // return strings;

        // ok? need deep copy
        return new ArrayList<>(strings);
    }
}
