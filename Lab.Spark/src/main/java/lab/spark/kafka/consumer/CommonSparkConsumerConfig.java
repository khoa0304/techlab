package lab.spark.kafka.consumer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;

public abstract class CommonSparkConsumerConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	
	
	private Map<String, Object> getKafkMapProperties(String kafkaServerList,String consumerGroup) {

		Map<String, Object> kafkaParams = new HashMap<>();
		kafkaParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerList);
		kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringDeserializer");
		kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringDeserializer");
		kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
		kafkaParams.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
		kafkaParams.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);

		return kafkaParams;

	}
	
	protected Map<String, Object> configConsumerGroupName(String kafkaServerList,String consumerGroup) {
		
		Map<String, Object> kafkaParams = getKafkMapProperties(kafkaServerList,consumerGroup);
		return kafkaParams;
	}
	
	protected String getKafkaServerList(Map<String,Object> kafkaParams) {
		return (String) kafkaParams.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG);
	}
	
	
}
