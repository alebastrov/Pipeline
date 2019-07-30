package com.nikondsl.streamreader.impl;

import com.nikondsl.streamreader.configuration.ReaderConfiguration;

public class PropertiesFileConfiguration extends DefaultConfiguration implements ReaderConfiguration {

    @Override
    public String[] getSinglelineCommentCharacters() {
        return new String[] {"#", "//"};
    }
}
