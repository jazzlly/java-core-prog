package com.ryan.java.demo.collection.dto;

import lombok.NonNull;

import java.util.Comparator;

public class CustomerComparator implements Comparator<Customer> {
    @Override
    public int compare(@NonNull Customer c1, @NonNull Customer c2) {

        if (c1.isVip.compareTo(c2.isVip) != 0) {
            return -c1.isVip.compareTo(c2.isVip);
        }

        if (c1.age.compareTo(c2.age) != 0) {
            return -c1.age.compareTo(c2.age);
        }
        return 0;
        // return c1.name.compareTo(c2.name);
    }
}
