package lab.spark.kafka.consumer;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;

public abstract class CommonSparkConsumerConfig {

	
	protected void configConsumerGroupName(Map<String,Object> kafkaParams,String consumerGroup) {
		
		kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, getClass().getName()+"-"+consumerGroup);
	}
}
