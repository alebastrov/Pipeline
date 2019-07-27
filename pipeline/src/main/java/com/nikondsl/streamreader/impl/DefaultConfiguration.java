package com.nikondsl.streamreader.impl;

import com.nikondsl.streamreader.ReaderConfiguration;

public class DefaultConfiguration implements ReaderConfiguration {
    private static ReaderConfiguration instance = new DefaultConfiguration();

    public static ReaderConfiguration getInstance() {
        return instance;
    }

    @Override
    public String getSinglelineCommentCharacters() {
        return null;
    }
}
