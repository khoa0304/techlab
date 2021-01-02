package lab.ui.kafka.consumer.manager;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lab.ui.kafka.consumer.CommonKafkaConsumerConfig;

public class UIKafkaConsumerRegistry {

	private static final Map<LocalDateTime, CommonKafkaConsumerConfig> 
				UI_KAFKA_CONSUMER_REGISTRY = new ConcurrentHashMap<>();
	
	private UIKafkaConsumerRegistry() {}
	
	public static void registerUiKafkaConsumerGroup(CommonKafkaConsumerConfig kafkaConsumerConfig) {
		UI_KAFKA_CONSUMER_REGISTRY.put(LocalDateTime.now(), kafkaConsumerConfig);
	}
	
	public static Map<LocalDateTime, CommonKafkaConsumerConfig> getUIKafkaConsumerGroup(){
		return UI_KAFKA_CONSUMER_REGISTRY;
	}
}
