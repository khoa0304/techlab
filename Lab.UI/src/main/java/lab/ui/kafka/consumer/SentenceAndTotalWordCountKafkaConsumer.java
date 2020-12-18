package lab.ui.kafka.consumer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lab.ui.model.WordAndCount;

public class SentenceAndTotalWordCountKafkaConsumer extends CommonKafkaConsumerConfig implements Callable<Void>{
	
	private Logger logger = LoggerFactory.getLogger(SentenceAndTotalWordCountKafkaConsumer.class); 

	private volatile boolean isStopped = false;
	
	private WordAndCount wordAndCount = new WordAndCount(new String[0], new long[0]);
	
	@Override
	public Void call() throws Exception {
		
		while (!isStopped) {
	
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(3));
			
			List<String> wordList = new ArrayList<>();
			List<Long> wordCountList = new ArrayList<>();
			for (ConsumerRecord<String, String> record : records) {
			
				logger.debug("received - offset = {}, key = {}, value = {}", record.offset(), record.key(), record.value());
				wordList.add(record.key());
				wordCountList.add(Long.valueOf(record.value()));
			}
			
			if(wordList.size() > 0 && wordCountList.size() > 0 && wordList.size() == wordCountList.size()) {
				
				wordAndCount.setsLabel(wordList.toArray(new String[wordList.size()]));
				
				long[] wordCountArray = new long[wordCountList.size()];
				int i = 0;
				for(Long count : wordCountList) {
					wordCountArray[i++] = count;
				}
				wordAndCount.setsData(wordCountArray);	
			}
			
		}
		
		return null;
	
	}
	
	public void stopped() {
		this.isStopped = true;
	}
	
	public WordAndCount getWordAndCount() {
		return this.wordAndCount;
	}
}
