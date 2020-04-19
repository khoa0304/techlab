package pdf.reader;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;

import org.testng.annotations.Test;

import lab.nlp.NLPProcessor;
import lab.pdf.reader.SimplePdfReader;

public class SimplePdfReaderTest {

	@Test(enabled=false)
	public void test1() throws IOException {
		
		URL pdfFileURL = getClass().getResource("/pdf/The-Data-Engineers-Guide-to-Apache-Spark.pdf");
		assertNotNull(pdfFileURL);
		
		SimplePdfReader simplePdfReader = new SimplePdfReader();
		String textContent = simplePdfReader.getPdfText(pdfFileURL);
		
		NLPProcessor nlpProcessor = new NLPProcessor();
		String[] sentences = nlpProcessor.extractSentences(textContent);
		
		for(String sentence : sentences) {

			System.out.println(sentence);
			String[] tokens = nlpProcessor.extractTokens(sentence);
			
			for(String token : tokens) {
				System.out.println(token);
			}
		}
	}
}
