package com.nikondsl.streamreader;

import java.util.function.Predicate;

public interface DoWithLine {

    void processNextLine(String line);

    default Predicate<String> getFilter() {
        return line -> line != null && !line.isEmpty();
    }
}
