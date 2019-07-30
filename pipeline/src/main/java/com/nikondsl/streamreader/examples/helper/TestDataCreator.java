package com.nikondsl.streamreader.examples.helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TestDataCreator {
	public static String invoke() throws IOException {
		String path = "/tmp/test.txt";
		System.err.println("Creating sample file...");
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path)));) {
			bw.write("\n");
			bw.write("\nFirst line,1;end of line");
			bw.write("\n     # comment line 1");
			bw.write("\nSecond line;      2    ;end");
			bw.write("\n# comment line 2");
			bw.write("\n; 3 ,");
			bw.write("\n // comment line 3");
			bw.write("\n;4;");
			bw.write("\n// comment line 4");
			bw.write("\n; 5;");
			bw.write("\n; 5;;Illegal");
			bw.write("\n;6;");
			bw.write("\n;Illegal ,");
			bw.write("\n;7 ;EOF");
		}
		return path;
	}
}
