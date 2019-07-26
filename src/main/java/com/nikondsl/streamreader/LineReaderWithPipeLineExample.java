package com.nikondsl.streamreader;

import com.google.common.primitives.Ints;
import com.nikondsl.streamreader.impl.Pipeline;
import com.nikondsl.streamreader.impl.PropertiesFileConfiguration;
import com.nikondsl.streamreader.util.CsvUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

public class LineReaderWithPipeLineExample {

    public static void main(String[] args) throws IOException {
        String path = "d:/logs/test.txt";
        System.err.println("Creating sample file...");
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path)));) {
            bw.write("\n");
            bw.write("\nFirst line,1;end of line");
            bw.write("\n# comment line 1");
            bw.write("\nSecond line;      2    ;end");
            bw.write("\n# comment line 2");
            bw.write("\n; 3 ,");
            bw.write("\n# comment line 3");
            bw.write("\n;4;");
            bw.write("\n# comment line 4");
            bw.write("\n; 5;");
            bw.write("\n; 5;;Illegal");
            bw.write("\n;6;");
            bw.write("\n;Illegal ,");
            bw.write("\n;7 ;EOF");
        }

        System.err.println("Processing file...");

        LineReader lineReader = new LineReader(path);
        lineReader.addToPipeline(new Pipeline<>("Skip comments", String.class, String[].class, new PropertiesFileConfiguration()) {
            @Override
            public String[] process(String line) {
                return CsvUtil.csvLineParser(line);
            }
        });
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