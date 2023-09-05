//package lab.ui.kafka.consumer.manager;
//
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import lab.ui.kafka.consumer.DocumentStatisticKafkaConsumer;
//import lab.ui.kafka.consumer.SentenceAndTotalWordCountKafkaConsumer;
//import lab.ui.kafka.consumer.WordCountKafkaConsumer;
//
//@Service
//public class UIKafkaConsumerGroupManager {
//	
//	@Value("${kafka.server.list}")
//	private String kafkaServerList;
//	
//	@Value("${spark.stream.sink.wordcount.topic}")
//	private String sparkStreamingSinkWordCountTopic;
//	
//	@Value("${spark.stream.sink.sentencecount.topic}")
//	private String sparkStreamingSinkSentenceCountTopic;
//	
//	@Value("${spark.stream.sink.sentence.topic:SentenceTopic}")
//	private String sparkStreamingSinkSentenceTopic;
//	
//	private UIKafkaConsumerGroupInitializer uiKafkaConsumerGroupInitializer;
//	
//	private ExecutorService scheduledExecutor = Executors.newFixedThreadPool(1);
//	
//	@PostConstruct
//	public void initUIKafkaConsumerGroup() {
//		
//		uiKafkaConsumerGroupInitializer = 
//				new UIKafkaConsumerGroupInitializer(kafkaServerList, 
//						new String[] {
//								sparkStreamingSinkWordCountTopic,
//								sparkStreamingSinkSentenceCountTopic,
//								sparkStreamingSinkSentenceTopic
//								});
//		scheduledExecutor.submit(uiKafkaConsumerGroupInitializer);
//
//	}
//	
//	@PreDestroy
//	public void shutdown() {
//		uiKafkaConsumerGroupInitializer.stopKafkaConsumer();
//	}
//
//	public SentenceAndTotalWordCountKafkaConsumer getSentenceAndTotalWordCountKafkaConsumer() {
//		return uiKafkaConsumerGroupInitializer.getSentenceAndTotalWordCountKafkaConsumer();
//	}
//
//	public WordCountKafkaConsumer getWordCountKafkaConsumer() {
//		return uiKafkaConsumerGroupInitializer.getWordCountKafkaConsumer();
//	}
//	
//	public DocumentStatisticKafkaConsumer getDocumentStatisticDTOList() {
//		return uiKafkaConsumerGroupInitializer.getDocumentStatisticKafkaConsumer();
//	}
//	
//
//}
