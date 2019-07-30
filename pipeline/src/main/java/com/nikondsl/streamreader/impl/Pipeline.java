package com.nikondsl.streamreader.impl;


import com.nikondsl.streamreader.LineProcessor;
import com.nikondsl.streamreader.configuration.ReaderConfiguration;
import com.nikondsl.streamreader.SkipProcessor;

public abstract class Pipeline<T, R> implements LineProcessor<T,R>, SkipProcessor<T> {

    private String name;
    private Class argument;
    private Class result;
    private ReaderConfiguration configuration;

    public Pipeline(Class<? extends T> argument, Class<? extends R> result) {
        this("", argument, result, DefaultConfiguration.getInstance());
    }

    public Pipeline(String name, Class<? extends T> argument, Class<? extends R> result) {
        this(name, argument, result, DefaultConfiguration.getInstance());
    }

    public Pipeline(Class<? extends T> argument, Class<? extends R> result, ReaderConfiguration configuration) {
        this("", argument, result, configuration);
    }

    public Pipeline(String name,
                    Class<? extends T> argument,
                    Class<? extends R> result,
                    ReaderConfiguration configuration) {
        this.argument = argument;
        this.result = result;
        if (configuration == null) throw new IllegalArgumentException("Please use DefaultConfiguration.getInstance or any ReaderConfiguration, null is not allowed here.");
        this.configuration = configuration;
        this.name = name;
    }

    public Class getArgumentClass() {
        return argument;
    }
    public Class getResultClass() {
        return result;
    }

    public ReaderConfiguration getConfiguration() {
        return configuration;
    }

    public R process(T argument) {
        return (R)argument;
    }

    public boolean isAllowed(T argument) {
        if (String.class.isAssignableFrom(argument.getClass())) {
            String line = ((String) argument).trim();
            String[] singlelineCommentCharacters = configuration.getSinglelineCommentCharacters();
            if (singlelineCommentCharacters == null || singlelineCommentCharacters.length == 0) {
                return true;
            }
            for (String startCommentSign : singlelineCommentCharacters) {
                if (line.startsWith(startCommentSign)) return false;
            }
            return true;
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
