package com.ryan.java.demo.concurrent.collection;


import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ConcurrentModificationException demo
 *      Concurrentlly modify a synchronized list while another thread
 *      is iterating it will trigger ConcurrentModificationException
 */
public class ConcurrentModificationDemo {

    private static final List<String> stringList =
            Collections.synchronizedList(new ArrayList<>());
    static {
        for (int i = 0; i < 10000; i++) {
            stringList.add(UUID.randomUUID().toString());
        }
    }

    /**
     * 单线程也可能抛出ConcurrentModificationException
     *  比如在迭代一个容器的时候，使用非iterator.remove()删除里面的成员
     */
    @Test
    public void smoke() {
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            queue.add(random.nextInt());
        }

        //  使用了iterator, 不会发生异常
        Iterator<Integer> iterator = queue.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }

        assertThat(queue).isEmpty();

        for (int i = 0; i < 10; i++) {
            queue.add(random.nextInt());
        }

        try {
            for (Integer integer : queue) {
                Integer foo = queue.poll(); // throw ConcurrentModificationException
            }
        } catch (Exception e) {
            e.printStackTrace();
            assertThat(e).isInstanceOf(ConcurrentModificationException.class);
        }
    }

    Runnable runnable = () -> {
        System.out.println("enter run ...");

        // fixme:
        // Concurrentlly modify a synchronized list while another thread
        // is iterating it will trigger ConcurrentModificationException
        for (String s : stringList) {
            System.out.println("enter loop ...");
            if (s.contains("1")) {
                System.out.println("remove str: " + s);
                stringList.remove(s);
                System.out.println("remove str after: " + s);
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    @Test
    public void smokeMultiThread() throws InterruptedException {
        Thread demo = new Thread(runnable, "demo-thread-1");
        Thread demo1 = new Thread(runnable, "demo-thread-2");
        demo.start();
        demo1.start();

        System.out.println("start join ...");
        demo.join();
        demo1.join();
        System.out.println("after join ...");
    }

}
