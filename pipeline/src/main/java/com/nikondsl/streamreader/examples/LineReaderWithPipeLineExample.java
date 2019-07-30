package com.nikondsl.streamreader.examples;

import com.google.common.primitives.Ints;
import com.nikondsl.streamreader.util.LineReader;
import com.nikondsl.streamreader.impl.Pipeline;
import com.nikondsl.streamreader.templates.transformers.CsvParserTemplate;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

public class LineReaderWithPipeLineExample {

    public static void main(String[] args) throws IOException {
        String path = TestDataCreator.invoke();

        System.err.println("Processing file...");

        LineReader lineReader = new LineReader(path);
        lineReader.addToPipeline(new Pipeline<String, String>("Skip comments", String.class, String.class) {
            @Override
            public boolean isAllowed(String line) {
                return !line.trim().startsWith("#");
            }
        });
        lineReader.addToPipeline(new CsvParserTemplate().createPipeline());
        lineReader.addToPipeline(new Pipeline<String[], String>("Should be 3 fields in a row", String[].class, String.class) {
            @Override
            public boolean isAllowed(String[] line) {
                return line.length == 3;
            }

            @Override
            public String process(String[] line) {
                return line[1];
            }
        });
        lineReader.addToPipeline(new Pipeline<String, String>("Trim field", String.class, String.class) {
            @Override
            public boolean isAllowed(String field) {
                return true;
            }

            @Override
            public String process(String field) {
                return field.trim();
            }
        });
        lineReader.addToPipeline(new Pipeline<String, Double>("Field should be able to be parsed as Integer", String.class, Double.class) {
            @Override
            public boolean isAllowed(String line) {
                return Ints.tryParse(line) != null;
            }

            @Override
            public Double process(String line) {
                return 10.0 * Ints.tryParse(line);
            }
        });

        Stream<CompletableFuture> stream = lineReader.readLineByLine(line -> line != null && !line.isEmpty());
        stream.forEach(x -> {
            try {
                System.err.println("Result: " + x.get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Interrupted!");
            } catch (ExecutionException e) {
//                System.err.println(e.getMessage());
            }
        });
    }
    
}
