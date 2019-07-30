package com.nikondsl.streamreader.templates.transformers;

import com.nikondsl.streamreader.impl.Pipeline;
import com.nikondsl.streamreader.impl.PropertiesFileConfiguration;
import com.nikondsl.streamreader.PipelineCreator;
import com.nikondsl.streamreader.util.CsvUtil;

public class CsvParserTemplate implements PipelineCreator {
	public Pipeline createPipeline() {
		return new Pipeline<String, String[]>("Parse as CSV", String.class, String[].class, new PropertiesFileConfiguration()) {
			@Override
			public String[] process(String line) {
				return CsvUtil.csvLineParser(line);
			}
		};
	}
}
