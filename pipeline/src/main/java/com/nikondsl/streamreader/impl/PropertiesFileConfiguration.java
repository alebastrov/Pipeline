package com.nikondsl.streamreader.impl;

import com.nikondsl.streamreader.ReaderConfiguration;
import com.nikondsl.streamreader.impl.DefaultConfiguration;

public class PropertiesFileConfiguration extends DefaultConfiguration implements ReaderConfiguration {

    @Override
    public String[] getSinglelineCommentCharacters() {
        return new String[] {"#", "//"};
    }
}
