package lab.spark.kafka.consumer;

import java.io.Serializable;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;

public abstract class CommonSparkConsumerConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	protected void configConsumerGroupName(Map<String,Object> kafkaParams,String consumerGroup) {
		
		kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, getClass().getName()+"-"+consumerGroup);
	}
	
	protected String getKafkaServerList(Map<String,Object> kafkaParams) {
		return (String) kafkaParams.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG);
	}
	
	
}
