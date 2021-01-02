package lab.ui.kafka.consumer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lab.ui.model.LabelAndCount;
import lab.ui.model.WordCountDTO;

public class WordCountKafkaConsumer extends CommonKafkaConsumerConfig {
	
	private Logger logger = LoggerFactory.getLogger(WordCountKafkaConsumer.class); 

	private volatile boolean isStopped = false;
	
	private LabelAndCount labelAndCount = new LabelAndCount(new String[0], new long[0]);
	
	private List<WordCountDTO> wordCountList = new ArrayList<>();
	
	public static final String CONSUMER_GROUP_NAME="UI-WordCount-Consumer-Group";
	
	private String sinkTopic = null;
	
	public WordCountKafkaConsumer(String sinkTopic) {
		registerConsumerGroup();
		this.sinkTopic = sinkTopic;
		
	}
	
	@Override
	public Void call() throws Exception {
		
		while (!isStopped) {
	
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
			
			List<String> wordLabels = new ArrayList<>();
			List<Long> wordCount = new ArrayList<>();
			List<WordCountDTO> localWordCountList = new ArrayList<>();
			for (ConsumerRecord<String, String> record : records) {
			
				logger.debug("received - offset = {}, key = {}, value = {}", record.offset(), record.key(), record.value());
				
				// to populate wordcount chart in index.htmlpage.
				wordLabels.add(record.key());
				wordCount.add(Long.valueOf(record.value()));
				
				// To populate wordcount table in index.html page
				WordCountDTO wordCountObj =  new WordCountDTO();
				wordCountObj.setWord(record.key());
				wordCountObj.setCount(Integer.valueOf(record.value()));
				localWordCountList.add(wordCountObj);
			}
			
			if(wordLabels.size() > 0 && wordCount.size() > 0 && wordLabels.size() == wordCount.size()) {
				
				labelAndCount.setsLabel(wordLabels.toArray(new String[wordLabels.size()]));
				
				long[] wordCountArray = new long[wordCount.size()];
				int i = 0;
				for(Long count : wordCount) {
					wordCountArray[i++] = count;
				}
				labelAndCount.setsData(wordCountArray);	
				this.wordCountList = localWordCountList;
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
	
	public List<WordCountDTO> getWordCountList() {
		return this.wordCountList;
	}
	
	@Override
	public String getConsumerGroupName() {
		return CONSUMER_GROUP_NAME;
	}

	@Override
	public String getSinkKafkaTopic() {
		return this.sinkTopic;
	}
}
