package lab.ui.kafka.consumer;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lab.ui.kafka.consumer.manager.UIKafkaConsumerRegistry;

public abstract class CommonKafkaConsumerConfig implements Callable<Void> {
	
	private Logger logger = LoggerFactory.getLogger(CommonKafkaConsumerConfig.class); 
	
	protected KafkaConsumer<String, String> consumer;
	
	protected void registerConsumerGroup() {
		UIKafkaConsumerRegistry.registerUiKafkaConsumerGroup(this);
	}
	

	public KafkaConsumer<String,String> createStringKeyValueConsumer(String brokerServerList) {

		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerServerList);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, getConsumerGroupName());
		//props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "180000");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
		
		consumer = new KafkaConsumer<String, String>(props);

		String topicName = getSinkKafkaTopic();
		consumer.subscribe(Arrays.asList(topicName));
		consumer.commitSync();
		logger.info("UI Service Subscribed to topic " + topicName);
	
		return consumer;
		
	}


	public abstract String getConsumerGroupName();
	public abstract String getSinkKafkaTopic();
	
}
