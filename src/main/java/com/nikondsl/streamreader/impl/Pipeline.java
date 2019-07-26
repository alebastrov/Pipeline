package com.nikondsl.streamreader.impl;


import com.nikondsl.streamreader.ProcessLineWorker;
import com.nikondsl.streamreader.ReaderConfiguration;
import com.nikondsl.streamreader.SkipLineProcessor;

public abstract class Pipeline<T, R> implements ProcessLineWorker<T,R>, SkipLineProcessor<T> {

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

    public abstract R process(T argument);

    public boolean isAllowed(T argument) {
        if (String.class.isAssignableFrom(argument.getClass())) {
            String line = (String) argument;
            String singlelineCommentCharacters = configuration.getSinglelineCommentCharacters();
            return singlelineCommentCharacters == null ||
                   singlelineCommentCharacters.isEmpty() ||
                   !line.startsWith(singlelineCommentCharacters);
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
