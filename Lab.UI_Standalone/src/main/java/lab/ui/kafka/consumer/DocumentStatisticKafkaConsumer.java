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
//
//public class DocumentStatisticKafkaConsumer extends CommonKafkaConsumerConfig implements Callable<Void>{
//	
//	private Logger logger = LoggerFactory.getLogger(DocumentStatisticKafkaConsumer.class); 
//
//	private volatile boolean isStopped = false;
//	
//	private List<DocumentStatisticDto> documentStatisticDTOList = new ArrayList<>();
//	
//	public static final String CONSUMER_GROUP_NAME="UI-DocumentStatistic-Consumer-Group";
//	
//	private String sinkTopic;
//	
//	public DocumentStatisticKafkaConsumer(String sinkTopic) {
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
//			for (ConsumerRecord<String, String> record : records) {
//			
//				logger.debug("received - offset = {}, key = {}, value = {}", record.offset(), record.key(), record.value());
//				
//				ObjectMapper objectMapper = new ObjectMapper();
//				DocumentStatisticDto documentStatisticDTO = objectMapper.readValue(record.value(), DocumentStatisticDto.class);
//				documentStatisticDTOList.add(documentStatisticDTO);
//			}
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
//	public List<DocumentStatisticDto>  getDocumentStatisticDTOList() {
//		return this.documentStatisticDTOList;
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
