package com.nikondsl.streamreader;

public interface SkipProcessor<T> {
    default boolean isAllowed(T argument) {return true;}
}
