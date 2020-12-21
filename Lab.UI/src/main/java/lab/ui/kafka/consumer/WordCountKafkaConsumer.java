package lab.ui.kafka.consumer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lab.ui.model.LabelAndCount;

public class WordCountKafkaConsumer extends CommonKafkaConsumerConfig implements Callable<Void>{
	
	private Logger logger = LoggerFactory.getLogger(WordCountKafkaConsumer.class); 

	private volatile boolean isStopped = false;
	
	private LabelAndCount labelAndCount = new LabelAndCount(new String[0], new long[0]);
	
	@Override
	public Void call() throws Exception {
		
		while (!isStopped) {
	
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(3));
			
			List<String> wordLabels = new ArrayList<>();
			List<Long> wordCount = new ArrayList<>();
			for (ConsumerRecord<String, String> record : records) {
			
				logger.debug("received - offset = {}, key = {}, value = {}", record.offset(), record.key(), record.value());
				wordLabels.add(record.key());
				wordCount.add(Long.valueOf(record.value()));
			}
			
			if(wordLabels.size() > 0 && wordCount.size() > 0 && wordLabels.size() == wordCount.size()) {
				
				labelAndCount.setsLabel(wordLabels.toArray(new String[wordLabels.size()]));
				
				long[] wordCountArray = new long[wordCount.size()];
				int i = 0;
				for(Long count : wordCount) {
					wordCountArray[i++] = count;
				}
				labelAndCount.setsData(wordCountArray);	
			}
			
		}
		
		return null;
	
	}
	
	public void stopped() {
		this.isStopped = true;
	}
	
	public LabelAndCount getWordAndCount() {
		return this.labelAndCount;
	}
}
