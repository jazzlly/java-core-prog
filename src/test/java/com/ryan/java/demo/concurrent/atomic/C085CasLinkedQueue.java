package com.ryan.java.demo.concurrent.atomic;

import net.jcip.annotations.ThreadSafe;
import org.junit.Test;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class C085CasLinkedQueue {
    @Test
    public void smoke() {
        ConcurrentLinkedQueue<Integer> integers = new ConcurrentLinkedQueue<>();
    }
}

/**
 * Insertion in the Michael-Scott nonblocking queue algorithm
 *
 * 需要画出头结点，尾节点, dummy, dummy.next, newNode, newNode.next
 */
@ThreadSafe
class LinkedQueue <E> {

    private static class Node <E> {
        final E item;
        final AtomicReference<Node<E>> next;

        public Node(E item, LinkedQueue.Node<E> next) {
            this.item = item;
            this.next = new AtomicReference<>(next);
        }
    }

    private final LinkedQueue.Node<E> dummy = new LinkedQueue.Node<>(null, null);
    private final AtomicReference<LinkedQueue.Node<E>> head
            = new AtomicReference<>(dummy);
    private final AtomicReference<LinkedQueue.Node<E>> tail
            = new AtomicReference<>(dummy);

    public boolean put(E item) {
        LinkedQueue.Node<E> newNode = new LinkedQueue.Node<E>(item, null);
        while (true) {
            LinkedQueue.Node<E> curTail = tail.get();
            LinkedQueue.Node<E> tailNext = curTail.next.get();
            if (curTail == tail.get()) {
                if (tailNext != null) {
                    // Queue in intermediate state, advance tail
                    // 中间状态：有一个线程已经操作了一半, 帮它完成下一半操作
                    // 在下一次where(true)循环时，完成自己的操作
                    tail.compareAndSet(curTail, tailNext);
                } else {
                    // In quiescent state, try inserting new node
                    // 静止状态：没有线程再操作
                    if (curTail.next.compareAndSet(null, newNode)) {
                        // Insertion succeeded, try advancing tail
                        tail.compareAndSet(curTail, newNode);
                        return true;
                    }
                }
            }
        }
    }
}

