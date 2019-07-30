package com.nikondsl.streamreader.examples;

import com.nikondsl.streamreader.examples.helper.TestDataCreator;
import com.nikondsl.streamreader.impl.Pipeline;
import com.nikondsl.streamreader.impl.ReadStatistics;
import com.nikondsl.streamreader.util.LineReader;

import java.io.IOException;

public class LineReaderExample {
	public static void main(String[] args) throws IOException {
		String path = TestDataCreator.invoke();
		
		System.err.println("Processing file...");
		new LineReader(path)
				.addToPipeline(new Pipeline<String, String>("copier", String.class, String.class) {
		})
				.setLogLevel(ReadStatistics.ToStringLevel.SILENT)
				.readLineByLine(line -> {System.err.println(line); return true;});
	}
}
