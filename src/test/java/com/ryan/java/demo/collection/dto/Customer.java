package com.ryan.java.demo.collection.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class Customer {
    @NonNull
    Boolean isVip;

    @NonNull
    Integer age;

    @NonNull
    String name;

    // @NonNull
    // Timestamp timestamp = new Timestamp(System.currentTimeMillis());
}
