package com.ryan.java.demo.collection;

import com.ryan.java.demo.collection.dto.Customer;
import com.ryan.java.demo.collection.dto.CustomerComparator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import static org.assertj.core.api.Assertions.assertThat;

public class PriorityQueueDemo {
    /**
     * 内部数据结构是排序的完全二叉树
     * 每次remove， poll的都是树中最小的元素
     *
     * add() and offer():
     *  add() throw exception while offer() return true/false
     *
     * remove() and poll():
     *  remove() throw exception wile poll() return true/false
     */

    @Test
    public void testNatureOrder() {
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        queue.add(10);
        queue.add(8);
        queue.add(20);
        queue.add(3);

        Integer e = null;
        ArrayList<Integer> integers = new ArrayList<>();
        while ((e = queue.poll()) != null) {
            integers.add(e);
        }

        assertThat(integers).isEqualTo(Arrays.asList(3, 8, 10, 20));
    }

    @Test
    public void PriorityQueue_withComparable() throws InterruptedException {
        PriorityQueue<Customer> customers = new PriorityQueue<>(new CustomerComparator());
        customers.add(new Customer(true, 80, "Albert"));
        customers.add(new Customer(false, 80, "Albert"));

        Customer customer = customers.poll();
        ArrayList<Customer> list = new ArrayList<>();
        while (customer != null) {
           list.add(customer);
           customer = customers.poll();
        }

        assertThat(list).isEqualTo(Arrays.asList(
                new Customer(false, 80, "Albert"),
                new Customer(true, 80, "Albert")
                ));


        customers.add(new Customer(false, 80, "Albert"));
        customers.add(new Customer(false, 35, "Albert"));
        list.clear();
        customer = customers.poll();
        while (customer != null) {
            list.add(customer);
            customer = customers.poll();
        }
        assertThat(list).isEqualTo(Arrays.asList(
                new Customer(false, 35, "Albert"),
                new Customer(false, 80, "Albert")
        ));

        list.clear();
        Customer c1 = new Customer(false, 35, "Newton");
        Customer c2 = new Customer(false, 35, "Albert");
        customers.add(c1);
        customers.add(c2);
        customer = customers.poll();
        while (customer != null) {
            list.add(customer);
            customer = customers.poll();
        }
        assertThat(list).isEqualTo(Arrays.asList(c2, c1));
    }
}

