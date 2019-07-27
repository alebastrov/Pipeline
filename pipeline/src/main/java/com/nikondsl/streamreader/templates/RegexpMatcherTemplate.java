package com.nikondsl.streamreader.templates;

import com.nikondsl.streamreader.impl.Pipeline;
import com.nikondsl.streamreader.impl.PropertiesFileConfiguration;

import java.util.regex.Pattern;

public class RegexpMatcherTemplate {
	private Pattern pattern;
	
	public RegexpMatcherTemplate(String patternString){
		pattern = Pattern.compile(patternString);
	}
	
	public Pipeline createPipeline() {
		return new Pipeline<String, Boolean>("Regular expression matcher", String.class, Boolean.class, new PropertiesFileConfiguration()) {
			@Override
			public Boolean process(String line) {
				return pattern.matcher(line).matches();
			}
		};
	}
}
