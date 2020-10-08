package lab.cassandra.repository.service;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import lab.cassandra.db.models.SentenceWords;

@SpringBootTest
public class SentenceWordsRepositoryTest extends AbstractTestNGSpringContextTests {

	
	@Autowired
	private SentenceWordsRepository sentenceWordsRepository;
	
	@Test
	public void testPersistToSentenceWords() {
		String fileName = "testFileName.pdf.txt";
		String sentence ="hellow word";
		List<String> words = new ArrayList<>();
		words.add(String.valueOf(System.currentTimeMillis()));
		words.add(String.valueOf(System.currentTimeMillis()));
			
		SentenceWords sentenceWords = new SentenceWords(fileName, sentence, words);
		sentenceWordsRepository.save(sentenceWords);
		
		sentenceWords = new SentenceWords(fileName, "hello khoa" , words);
		sentenceWordsRepository.save(sentenceWords);
		
		List<SentenceWords> sentenceWordsFromDB = sentenceWordsRepository.findAllByFileName(fileName);
		assertEquals(sentenceWordsFromDB.get(0), sentenceWords);
	}
	
}
