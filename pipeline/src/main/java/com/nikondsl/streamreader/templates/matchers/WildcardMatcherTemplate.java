package com.nikondsl.streamreader.templates.matchers;

import com.nikondsl.streamreader.impl.Pipeline;
import com.nikondsl.streamreader.impl.PropertiesFileConfiguration;

import java.util.regex.Pattern;

public class WildcardMatcherTemplate {
	private Pattern pattern;
	
	public WildcardMatcherTemplate(String patternString){
		pattern = Pattern.compile(patternString
				.replaceAll("\\*",".*")
				.replaceAll("\\?",".?")
				.replaceAll("\\+",".+"));
	}
	
	public Pipeline createPipeline() {
		return new Pipeline<String, Boolean>("Wildcard matcher", String.class, Boolean.class, new PropertiesFileConfiguration()) {
			@Override
			public boolean isAllowed(String line) {
				return pattern.matcher(line).matches();
			}
		};
	}
}
