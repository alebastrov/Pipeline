package com.nikondsl.streamreader.util;

import java.util.ArrayDeque;

public class MovingAverage {
    private ArrayDeque<Double> window = new ArrayDeque<>();
    private final int size;
    private double sum;

    public MovingAverage(int size) {
        if (size < 1 || size > 1_000_000) throw new IllegalArgumentException(size+" is not in allowed range [1..1000000]");
        this.size = size;
    }

    public double next(double val) {
        if (window.size() >= size) {
            sum -= window.removeFirst();
        }
        sum += val;
        window.addLast(val);
        return sum / window.size();
    }
    
    @Override
    public String toString() {
        return "MovingAverage{" +
                "" + window +
                '}';
    }
}
