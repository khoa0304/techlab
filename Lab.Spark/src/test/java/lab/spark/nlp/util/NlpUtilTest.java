package lab.spark.nlp.util;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Set;

import org.testng.annotations.Test;

import lab.spark.nlp.util.NlpUtil;

public class NlpUtilTest {

	@Test
	public void testLoadEnglishStopWords() throws IOException {
		NlpUtil nlpUtil = new NlpUtil();
		Set<String> stopWords = nlpUtil.getStopWordsSet();
		assertTrue(stopWords.size() > 0);
	}
}
