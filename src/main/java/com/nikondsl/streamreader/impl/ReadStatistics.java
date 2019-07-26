package com.nikondsl.streamreader.impl;

import com.nikondsl.streamreader.util.MovingAverage;

public class ReadStatistics {
    private double fileSize;
    private volatile double position;
    private String message;
    private ToStringLevel level = ToStringLevel.PERCENT;
    private volatile long startTime = System.nanoTime();
    private String speedKph = "?";
    private String line;
    private MovingAverage movingAverage = new MovingAverage(7);

    public void setLine(String line) {
        this.line = line;
    }

    public enum ToStringLevel {
        SHORT_PERCENT,
        SHORT_NUMBERS,
        PERCENT,
        NUMBERS;
    }

    public void setLevel(ToStringLevel level) {
        this.level = level;
    }

    public ReadStatistics(long fileSize) {
        this.fileSize = fileSize;
    }

    public void addToPosition(long position) {
        this.position += position;
        fixPosition();
    }

    public void setPosition(long position) {
        this.position = position;
        fixPosition();
    }


    private void fixPosition() {
        if (position > fileSize) position = fileSize;
        double speed = position/((System.nanoTime() - startTime)/1_000_000_000.0/3_600.0);
        speed = movingAverage.next(speed);
        int count = 0;
        String extension = "";
        while ((speed = speed/1024.0)>1.0) count++;
        switch (count) {
            case 1: extension = "kph";
                    break;
            case 2: extension = "Mph";
                    break;
            case 3: extension = "Gph";
                    break;
            case 4: extension = "Tph";
                    break;
            case 5: extension = "Pph";
                    break;
            default: extension = "ph";
        }
        speedKph = String.format("%7.2f %s", speed*1024.0, extension);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toString() {
        switch (level) {
            case NUMBERS:
                return String.format("Processed chars: %8d/%-8d", (long)position, (long)fileSize) + "" + speedKph + " (" + message + ")";
            case PERCENT:
                return "Processed " + String.format("%6.2f %%", (position*100.0/fileSize)) + speedKph +", (" + message + ")";
            case SHORT_NUMBERS:
                return (long)position + "/" + (long)fileSize;
            case SHORT_PERCENT:
                return String.format("%6.2f %%", (position*100.0/fileSize)) + ", "+ speedKph;
        }
        return "Processing " + position + "/" + fileSize + ". (" + message + ")";
    }
}