package com.nikondsl.streamreader.util;

import java.util.ArrayDeque;

public class MovingAverage {
    private ArrayDeque<Double> window;
    private final int size;
    private double sum;

    public MovingAverage(int size) {
        window = new ArrayDeque<>();
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
}
