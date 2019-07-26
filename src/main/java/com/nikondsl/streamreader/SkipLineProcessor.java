package com.nikondsl.streamreader;

public interface SkipLineProcessor<T> {
    default boolean isAllowed(T argument) {return true;}
}
