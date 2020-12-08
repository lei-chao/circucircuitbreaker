package com.circuircuitbreaker;

import java.util.Iterator;
import java.util.Random;

public class DataProviderTest implements Iterator<Object[]> {

    public DataProviderTest(int bound, int size) {
        this.bound = bound;
        this.size = size;
    }

    private Random random = new Random();

    private int bound;

    private int size;

    private int used;

    public boolean hasNext() {
        return used < size;
    }

    public Object[] next() {
        used++;
        return new Object[]{random.nextInt(bound)};
    }
}