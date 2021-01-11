package lab.ui.kafka.consumer.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lab.ui.kafka.consumer.CommonKafkaConsumerConfig;

public class UIKafkaConsumerRegistry {

	private static final Map<String, CommonKafkaConsumerConfig> 
				UI_KAFKA_CONSUMER_REGISTRY = new ConcurrentHashMap<>();
	
	private UIKafkaConsumerRegistry() {}
	
	public static void registerUiKafkaConsumerGroup(CommonKafkaConsumerConfig kafkaConsumerConfig) {
		UI_KAFKA_CONSUMER_REGISTRY.put(kafkaConsumerConfig.getClass().getName(), kafkaConsumerConfig);
	}
	
	public static Map<String, CommonKafkaConsumerConfig> getUIKafkaConsumerGroup(){
		return UI_KAFKA_CONSUMER_REGISTRY;
	}
}
