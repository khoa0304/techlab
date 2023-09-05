//package lab.ui.kafka.consumer;
//
//import java.time.Duration;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.Callable;
//
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.apache.kafka.clients.consumer.ConsumerRecords;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import lab.common.file.dto.DocumentStatisticDto;
//import lab.ui.model.LabelAndCount;
//
//public class SentenceAndTotalWordCountKafkaConsumer extends CommonKafkaConsumerConfig implements Callable<Void>{
//	
//	private Logger logger = LoggerFactory.getLogger(SentenceAndTotalWordCountKafkaConsumer.class); 
//
//	private volatile boolean isStopped = false;
//	
//	private LabelAndCount wordAndCount = new LabelAndCount(new String[0], new long[0]);
//	
//	public static final String CONSUMER_GROUP_NAME="UI-SentenceCount-Consumer-Group";
//	
//	private String sinkTopic;
//	
//	public SentenceAndTotalWordCountKafkaConsumer(String sinkTopic) {
//		registerConsumerGroup();
//		this.sinkTopic = sinkTopic;
//	}
//	
//	@Override
//	public Void call() throws Exception {
//		
//		while (!isStopped) {
//	
//			ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
//			
//			List<String> fileNameLabels = new ArrayList<>();
//			List<Long> sentenceCountList = new ArrayList<>();
//		
//			for (ConsumerRecord<String, String> record : records) {
//			
//				logger.debug("received - offset = {}, key = {}, value = {}", record.offset(), record.key(), record.value());
//				ObjectMapper objectMapper = new ObjectMapper();
//				DocumentStatisticDto documentStatisticDTO = objectMapper.readValue(record.value(), DocumentStatisticDto.class);
//		
//				fileNameLabels.add(documentStatisticDTO.getFileName());
//				sentenceCountList.add(Long.valueOf(documentStatisticDTO.getTotalSentences()));
//			}
//			
//			if(fileNameLabels.size() > 0 && sentenceCountList.size() > 0 && fileNameLabels.size() == sentenceCountList.size()) {
//				
//				wordAndCount.setsLabel(fileNameLabels.toArray(new String[fileNameLabels.size()]));
//				
//				long[] wordCountArray = new long[sentenceCountList.size()];
//				int i = 0;
//				for(Long count : sentenceCountList) {
//					wordCountArray[i++] = count;
//				}
//				wordAndCount.setsData(wordCountArray);	
//			}
//			
//		}
//		
//		return null;
//	
//	}
//	
//	public void stopped() {
//		this.isStopped = true;
//	}
//	
//	public LabelAndCount getWordAndCount() {
//		return this.wordAndCount;
//	}
//	
//	@Override
//	public String getConsumerGroupName() {
//		return CONSUMER_GROUP_NAME;
//	}
//
//	@Override
//	public String getSinkKafkaTopic() {
//		return this.sinkTopic;
//	}
//}
