package com.nikondsl.streamreader;

public interface ProcessLineWorker<T,R> {
    default R process(T argument) {return null;}
}
