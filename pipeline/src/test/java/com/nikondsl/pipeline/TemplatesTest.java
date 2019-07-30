package com.nikondsl.pipeline;

import com.nikondsl.streamreader.impl.Pipeline;
import com.nikondsl.streamreader.templates.matchers.RegexpMatcherTemplate;
import com.nikondsl.streamreader.templates.matchers.WildcardMatcherTemplate;
import com.nikondsl.streamreader.templates.transformers.CsvParserTemplate;
import com.nikondsl.streamreader.templates.transformers.FindReplaceTemplate;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TemplatesTest {
	@Test
	public void csvParserTest() {
		Pipeline<String, String[]> pipeline = new CsvParserTemplate().createPipeline();
		String[] result = pipeline.process(";,;4 , 5");
		assertArrayEquals(new String[]{"", "", "", "4 ", " 5"}, result);
	}
	
	@Test
	public void replacerTest() {
		Pipeline<String, String> pipeline = new FindReplaceTemplate("\\w+?(\\d+)\\w+","$1").createPipeline();
		String result = pipeline.process("aaa12345bbb");
		assertEquals("12345", result);
	}
	
	@Test
	public void wildcardTest() {
		Pipeline<String, String> pipeline = new WildcardMatcherTemplate("aaa?").createPipeline();
		assertTrue(pipeline.isAllowed("aaa"));
		assertTrue(pipeline.isAllowed("aaaa"));
		assertFalse(pipeline.isAllowed("aaaaa"));
		assertFalse(pipeline.isAllowed("bbb"));
	}
	
	@Test
	public void regexTest() {
		Pipeline<String, String> pipeline = new RegexpMatcherTemplate("\\w+?\\d+\\w+").createPipeline();
		assertTrue(pipeline.isAllowed("aaa12345bbb"));
		assertTrue(pipeline.isAllowed("12345bbb"));
		assertFalse(pipeline.isAllowed("aaabbb"));
	}
}
