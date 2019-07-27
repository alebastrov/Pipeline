package com.nikondsl.streamreader.templates;

import com.nikondsl.streamreader.impl.Pipeline;
import com.nikondsl.streamreader.impl.PropertiesFileConfiguration;

import java.util.regex.Pattern;

public class WildcardMatcherTemplate {
	private Pattern pattern;
	
	public WildcardMatcherTemplate(String patternString){
		pattern = Pattern.compile(patternString
				.replaceAll("*",".*")
				.replaceAll("?",".?")
				.replaceAll("+",".+"));
	}
	
	public Pipeline createPipeline() {
		return new Pipeline<String, Boolean>("Wildcards matcher", String.class, Boolean.class, new PropertiesFileConfiguration()) {
			@Override
			public Boolean process(String line) {
				return pattern.matcher(line).matches();
			}
		};
	}
}
