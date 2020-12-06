package lab.spark.kafka.consumer;

import java.net.UnknownHostException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import lab.spark.config.OpenNLPConfigService;
import lab.spark.config.SparkConfigService;

public class KafkaConsumerTask implements Runnable {

	private Logger logger = LoggerFactory.getLogger(KafkaConsumerTask.class);
	
	private SparkConfigService sparkConfigService;
	private  Map<String, Object> configMap;
	private String topicName;
	
	private RestTemplate restTemplate;
	private String kafkaServiceName;
	
	public KafkaConsumerTask(
			SparkConfigService sparkConfigService,
			Map<String, Object> configMap, 
			String topicName,
			OpenNLPConfigService openNLPConfig,
			RestTemplate restTemplate,
			String kafkaServiceName) {
		
		this.sparkConfigService = sparkConfigService;
		this.configMap = configMap;
		this.topicName = topicName;
		this.restTemplate = restTemplate;
		this.kafkaServiceName = kafkaServiceName;

	}
	@Override
	public void run() {
		try {
			//final SparkOpenNlpProcessor sparkOpenNlpService = new SparkOpenNlpProcessor();		
			FileUploadConsumerTestTask fileUploadConsumerTestTask = 
					new FileUploadConsumerTestTask();
			
			fileUploadConsumerTestTask.processFileUpload(
					sparkConfigService.getSparkConfig(FileUploadContentConsumerService.class.getName()), 
					configMap, topicName,restTemplate,kafkaServiceName);
			
		} catch (UnknownHostException | InterruptedException | ClassNotFoundException e) {
			logger.warn("", e);
		}
	}

}
