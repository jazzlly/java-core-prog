package com.ryan.java.demo.concurrent;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.NotThreadSafe;
import net.jcip.annotations.ThreadSafe;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 介绍一些线程安全对象的设计原则
 */
public class C0012ComposingObjectDesign {
}

/**
 * 设计线程安全的类：
 *
 * 对象的状态是由哪些变量构成的
 * 确定不变约束
 *  尽可能使用不变对象？
 * 确定并发访问策略
 */

/**
 * 使用final定义counter避免了继承对类的修改
 * 使用了synchronized的并发访问控制策略
 *  java监视器模式
 */
@ThreadSafe
final class Counter {
    @GuardedBy("this") private int count;

    public synchronized int getCount() {
        return count;
    }

    public synchronized int incCount() {
        if (count == Integer.MAX_VALUE) {
            throw new IllegalStateException("counter overflow");
        }
        return ++count;
    }
}

/**
 * 使用synchronized作为同步机制
 *  "Java监视器模式"
 */
@ThreadSafe
final class PersonSet {

    @Immutable
    private static class Person {
        private final String name;

        public Person(String name) {
            this.name = name;
        }
    }

    @GuardedBy("this")
    Set<Person> personSet = new HashSet<>();

    public synchronized void addPerson(Person person) {
        personSet.add(person);
    }

    public synchronized boolean containPersion(Person person) {
        return personSet.contains(person);
    }
}

/**
 * 私有锁保护状态
 *  私有锁比java监视器更灵活
 */
@ThreadSafe
class PrivateLockDemo {
    private Object lock = new Object();

    @GuardedBy("lock")
    private Set<String> stringSet = new HashSet<>();

    void addString(String string) {
        synchronized (lock) {
            stringSet.add(string);
        }
    }
}


/**
 * 使用synchronized + deepcopy传入，传出对象
 *
 * MonitorVehicleTracker
 * Monitor-based vehicle tracker implementation
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
class MonitorVehicleTracker {
    @GuardedBy("this") private final Map<String, MutablePoint> locations;

    /**
     * 使用deepcopy拷贝传输的数据
     * @param locations
     */
    public MonitorVehicleTracker(Map<String, MutablePoint> locations) {
        this.locations = deepCopy(locations);
    }

    /**
     * 使用deepcopy返回数据
     * @return
     */
    public synchronized Map<String, MutablePoint> getLocations() {
        return deepCopy(locations);
    }

    public synchronized MutablePoint getLocation(String id) {
        MutablePoint loc = locations.get(id);
        return loc == null ? null : new MutablePoint(loc);
    }

    public synchronized void setLocation(String id, int x, int y) {
        MutablePoint loc = locations.get(id);
        if (loc == null)
            throw new IllegalArgumentException("No such ID: " + id);
        loc.x = x;
        loc.y = y;
    }

    private static Map<String, MutablePoint> deepCopy(Map<String, MutablePoint> m) {
        Map<String, MutablePoint> result = new HashMap<>();

        for (String id : m.keySet()) {
            result.put(id, new MutablePoint(m.get(id)));
        }

        return Collections.unmodifiableMap(result);
    }
}

@NotThreadSafe
class MutablePoint {
    public int x, y;

    public MutablePoint() {
        x = 0;
        y = 0;
    }

    public MutablePoint(MutablePoint p) {
        this.x = p.x;
        this.y = p.y;
    }
}


@Immutable
class Point {
    public final int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

/**
 * DelegatingVehicleTracker
 * <p/>
 * Delegating thread safety to a ConcurrentHashMap
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
class DelegatingVehicleTracker {
    private final ConcurrentMap<String, Point> locations;

    public DelegatingVehicleTracker(Map<String, Point> points) {
        locations = new ConcurrentHashMap<>(points);
    }

    public Point getLocation(String id) {
        return locations.get(id);
    }

    public void setLocation(String id, int x, int y) {
        if (locations.replace(id, new Point(x, y)) == null)
            throw new IllegalArgumentException("invalid vehicle name: " + id);
    }

    // Alternate version of getLocations (Listing 4.8)
    // 对于不可变的对象，可以不需要deepcopy
    // 会和setLocation发成冲突？
    public Map<String, Point> getLocationsAsStatic() {
        return Collections.unmodifiableMap(
                new HashMap<String, Point>(locations));
    }
}

/**
 * ListHelder
 * <p/>
 * Examples of thread-safe and non-thread-safe implementations of
 * put-if-absent helper methods for List
 *
 * @author Brian Goetz and Tim Peierls
 */

@NotThreadSafe
class BadListHelper <E> {
    public List<E> list = Collections.synchronizedList(new ArrayList<E>());

    public synchronized boolean putIfAbsent(E x) {
        boolean absent = !list.contains(x);
        if (absent)
            list.add(x);
        return absent;
    }
}

@ThreadSafe
class GoodListHelper <E> {
    public List<E> list = Collections.synchronizedList(new ArrayList<E>());

    public boolean putIfAbsent(E x) {
        synchronized (list) {
            boolean absent = !list.contains(x);
            if (absent)
                list.add(x);
            return absent;
        }
    }
}



