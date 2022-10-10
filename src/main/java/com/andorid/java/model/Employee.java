package com.andorid.java.model;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class Employee {
    private String name;
    private long ageInSecs;

    public Employee(String name) {
        this.name = name;
        ageInSecs =  LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAgeInSecs() {
        return ageInSecs;
    }
}
