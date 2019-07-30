package com.nikondsl.streamreader.templates.transformers;

import com.nikondsl.streamreader.PipelineCreator;
import com.nikondsl.streamreader.impl.Pipeline;
import com.nikondsl.streamreader.impl.PropertiesFileConfiguration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindReplaceTemplate implements PipelineCreator {
	private Pattern pattern;
	private String replace;
	
	public FindReplaceTemplate(String patternString, String replace){
		pattern = Pattern.compile(patternString);
		this.replace = replace;
	}
	
	public Pipeline createPipeline() {
		return new Pipeline<String, String>("Regular expression replacer", String.class, String.class, new PropertiesFileConfiguration()) {
			@Override
			public String process(String line) {
				Matcher matcher =  pattern.matcher(line);
				if (matcher.find()) line = matcher.replaceAll(replace);
				return line;
			}
		};
	}
}
