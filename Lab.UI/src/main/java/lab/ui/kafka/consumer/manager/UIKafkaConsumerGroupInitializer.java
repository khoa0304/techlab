package lab.ui.kafka.consumer.manager;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import lab.ui.kafka.consumer.CommonKafkaConsumerConfig;
import lab.ui.kafka.consumer.DocumentStatisticKafkaConsumer;
import lab.ui.kafka.consumer.SentenceAndTotalWordCountKafkaConsumer;
import lab.ui.kafka.consumer.WordCountKafkaConsumer;

public class UIKafkaConsumerGroupInitializer implements Callable<Void>{
	
	//private Map<String,CommonKafkaConsumerConfig> kafkaConsumerGroupPerSegment = new ConcurrentHashMap<>();
	
	private ExecutorService scheduledExecutor = Executors.newFixedThreadPool(10, new ThreadFactory() {
		
		@Override
		public Thread newThread(Runnable thread) {
			   Thread t = Executors.defaultThreadFactory().newThread(thread);
               t.setDaemon(true);
               return t;
		}
	});
	
	private String kafkaServerList = null;
	
	private WordCountKafkaConsumer wordCountKafkaConsumer;
	private SentenceAndTotalWordCountKafkaConsumer sentenceAndTotalWordCountKafkaConsumer;
	private DocumentStatisticKafkaConsumer documentStatisticKafkaConsumer;
	
	public UIKafkaConsumerGroupInitializer(String kafkaServerList,String[] sinkKafkaTopic ) {
		this.kafkaServerList = kafkaServerList;
		wordCountKafkaConsumer = new WordCountKafkaConsumer(sinkKafkaTopic[0]);
		sentenceAndTotalWordCountKafkaConsumer = new SentenceAndTotalWordCountKafkaConsumer(sinkKafkaTopic[2]);
		documentStatisticKafkaConsumer = new DocumentStatisticKafkaConsumer(sinkKafkaTopic[2]);
	}
	
	@Override
	public Void call() throws Exception {
	
		Map<LocalDateTime, CommonKafkaConsumerConfig> UI_KAFKA_CONSUMER_REGISTRY = UIKafkaConsumerRegistry.getUIKafkaConsumerGroup();
		
		for(Map.Entry<LocalDateTime, CommonKafkaConsumerConfig> entry : UI_KAFKA_CONSUMER_REGISTRY.entrySet()) {
			
			CommonKafkaConsumerConfig consumerGroup = entry.getValue();
			consumerGroup.createStringKeyValueConsumer(kafkaServerList);
			scheduledExecutor.submit(consumerGroup);
		}
		return null;
	}

	public void stopKafkaConsumer() {
		wordCountKafkaConsumer.stopped();
		sentenceAndTotalWordCountKafkaConsumer.stopped();
		documentStatisticKafkaConsumer.stopped();
	}

	public SentenceAndTotalWordCountKafkaConsumer getSentenceAndTotalWordCountKafkaConsumer() {
		return this.sentenceAndTotalWordCountKafkaConsumer;
	}

	public WordCountKafkaConsumer getWordCountKafkaConsumer() {
		return this.wordCountKafkaConsumer;
	}
	
	public DocumentStatisticKafkaConsumer getDocumentStatisticKafkaConsumer() {
		return this.documentStatisticKafkaConsumer;
	}
}
