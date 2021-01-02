package lab.solr.kafka.consumer;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lab.common.file.dto.SentenceWordDto;

public abstract class CommonKafkaConsumerConfig {
	
	private Logger logger = LoggerFactory.getLogger(CommonKafkaConsumerConfig.class); 
	
	protected KafkaConsumer<String, SentenceWordDto> consumer;
	

	public KafkaConsumer<String,SentenceWordDto> createSentenceWordDtoConsumer(
			String brokerServerList, String topicName, String groupName) {

		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerServerList);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupName);
		//props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "180000");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
		//props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "lab.solr.kafka.serializer.SentenceWordDtoDeserializer");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
		
		consumer = new KafkaConsumer<String, SentenceWordDto>(props);

		consumer.subscribe(Arrays.asList(topicName));
		consumer.commitSync();
		logger.info("Sorl Service Subscribed to topic " + topicName);
	
		return consumer;
		
	}
}
