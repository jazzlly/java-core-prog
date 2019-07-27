package com.ryan.java.demo.collection;

import com.ryan.java.demo.collection.dto.Customer;
import com.ryan.java.demo.collection.dto.CustomerComparator;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class PriorityBlockingQueueDemo {
    /**
     * add() and offer():
     * add() throw exception while offer() return true/false
     * <p>
     * remove() and poll():
     * remove() throw exception wile poll() return true/false
     */

    private static final PriorityBlockingQueue<Customer> CUSTOMERS
            = new PriorityBlockingQueue<Customer>(11,
                new CustomerComparator());

    private static final ArrayList<Customer> RESULT_LIST =
            new ArrayList<>();

    static class MsgProducer extends Thread {
        Random random = new Random();
        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                Customer customer = new Customer(random.nextBoolean(),
                        Math.abs(random.nextInt(100)), UUID.randomUUID().toString());
                CUSTOMERS.add(customer);
            }
            log.info("Producer thread done!");
        }
    }

    static class MsgConsumer extends Thread {
        @Override
        public void run() {
            Customer customer = CUSTOMERS.poll();
            while (customer != null) {
                log.info(customer.toString());
                RESULT_LIST.add(customer);
                try {
                    customer = CUSTOMERS.poll(1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    log.warn("", e);
                }
            }
            log.info("Consumer thread done!");
        }
    }

    @Test
    public void smoke() throws InterruptedException {
        MsgProducer msgProducer = new MsgProducer();
        MsgConsumer msgConsumer = new MsgConsumer();

        log.info("producer start ...");
        msgProducer.start();
        Thread.sleep(2_000);
        log.info("consumer start ...");
        msgConsumer.start();

        Thread.sleep(5_000);
        log.info("begin join ...");
        msgConsumer.join();
        msgProducer.join();
        log.info("join done!");

        assertThat(RESULT_LIST).isSortedAccordingTo(new CustomerComparator());
    }
}


