package lab.spark.kafka.consumer;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lab.spark.config.OpenNLPConfigService;
import lab.spark.config.SparkConfigService;

public class KafkaConsumerTask implements Runnable {

	private Logger logger = LoggerFactory.getLogger(KafkaConsumerTask.class);
	
	private SparkConfigService sparkConfigService;
	private  Map<String, Object> configMap;
	
	private String topicName;

	private String sparkStreamingSinkTopicList;
	
	public KafkaConsumerTask(
			SparkConfigService sparkConfigService,
			Map<String, Object> configMap, 
			String topicName,
			OpenNLPConfigService openNLPConfig,
			String sparkStreamingSinkTopicList) {
		
		this.sparkConfigService = sparkConfigService;
		this.configMap = configMap;
		this.topicName = topicName;
		this.sparkStreamingSinkTopicList = sparkStreamingSinkTopicList;
	}
	@Override
	public void run() {
		try {
			//final SparkOpenNlpProcessor sparkOpenNlpService = new SparkOpenNlpProcessor();		
			FileUploadConsumerTestTask fileUploadConsumerTestTask = 
					new FileUploadConsumerTestTask();
			
			fileUploadConsumerTestTask.processFileUpload(
					sparkConfigService.getSparkConfig(FileUploadContentConsumerService.class.getName()), 
					configMap, topicName,sparkStreamingSinkTopicList);
			
		} catch (Exception e) {
			logger.warn("", e);
		}
	}

}
