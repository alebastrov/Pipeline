package com.nikondsl.streamreader;

public interface LineProcessor<T,R> {
    default R process(T argument) {return null;}
}
