package lab.spark.kafka.consumer;

import java.net.UnknownHostException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lab.spark.config.SparkConfigService;

public class KafkaConsumerTask implements Runnable {

	private Logger logger = LoggerFactory.getLogger(KafkaConsumerTask.class);
	
	private SparkConfigService sparkConfigService;
	private  Map<String, Object> configMap;
	private String topicName;
	
	public KafkaConsumerTask(SparkConfigService sparkConfigService, Map<String, Object> configMap, String topicName) {
		
		this.sparkConfigService = sparkConfigService;
		this.configMap = configMap;
		this.topicName = topicName;
		
	}
	@Override
	public void run() {
		try {
			
			new FileUploadConsumerTestTask(
					sparkConfigService.getSparkConfig(FileUploadContentConsumerService.class.getName()),
					this.configMap, this.topicName);
		} catch (UnknownHostException | InterruptedException e) {
			logger.warn("", e);
		}
	}

}
