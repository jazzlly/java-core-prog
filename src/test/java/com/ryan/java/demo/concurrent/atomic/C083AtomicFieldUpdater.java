package com.ryan.java.demo.concurrent.atomic;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Many voters (threads) vote for 3 candidates concurrently
 * Checking whether the total score of the candidates is correct
 *
 *  不修改内的实现，就能够为类中的volatile成员新增原子性的特性
 */
public class C083AtomicFieldUpdater {

    @Data
    @AllArgsConstructor
    static class Candidate {
        volatile String name;
        volatile int score;
    }

    static CopyOnWriteArrayList<Candidate> candidates = new CopyOnWriteArrayList<>();
    static {
        candidates.addAll(Arrays.asList(
                new Candidate("foo", 0),
                new Candidate("bar", 0),
                new Candidate("wah", 0)));
    }

    static AtomicIntegerFieldUpdater<Candidate> updater =
            AtomicIntegerFieldUpdater.newUpdater(Candidate.class, "score");

    // fixme: demo
    static AtomicReferenceFieldUpdater<Candidate, String> referenceFieldUpdater =
           AtomicReferenceFieldUpdater.newUpdater(Candidate.class, String.class, "name");


    @Test
    public void smoke() throws InterruptedException {
        final int voterCount = 50_000;
        ExecutorService service = Executors.newFixedThreadPool(8);
        for (int i = 0; i < voterCount; i++) {
            service.execute(voter);
        }

        service.shutdown();
        service.awaitTermination(10, TimeUnit.SECONDS);

        int sum = candidates.stream().mapToInt(Candidate::getScore).sum();
        assertThat(sum).isEqualTo(voterCount);
    }

    static Runnable voter = () -> {
        int index = (int) (Math.random() * 3);
        updater.getAndIncrement(candidates.get(index));
    };
}
