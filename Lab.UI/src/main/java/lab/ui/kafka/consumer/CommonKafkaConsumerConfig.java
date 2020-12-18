package lab.ui.kafka.consumer;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CommonKafkaConsumerConfig {
	
	private Logger logger = LoggerFactory.getLogger(CommonKafkaConsumerConfig.class); 
	
	protected KafkaConsumer<String, String> consumer;
	

	public KafkaConsumer<String,String> createStringKeyValueConsumer(
			String brokerServerList, String topicName, String groupName) {

		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerServerList);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupName);
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
		consumer = new KafkaConsumer<String, String>(props);

		consumer.subscribe(Arrays.asList(topicName));
		logger.info("Subscribed to topic " + topicName);
	
		return consumer;
		
	}
}
