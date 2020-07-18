package lab.spark.kafka.consumer;

import java.net.UnknownHostException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lab.spark.config.OpenNLPConfigService;
import lab.spark.config.SparkConfigService;
import lab.spark.model.SparkOpenNlpProcessor;

public class KafkaConsumerTask implements Runnable {

	private Logger logger = LoggerFactory.getLogger(KafkaConsumerTask.class);
	
	private SparkConfigService sparkConfigService;
	private  Map<String, Object> configMap;
	private String topicName;
	
	private OpenNLPConfigService openNLPConfig;
	
	public KafkaConsumerTask(
			SparkConfigService sparkConfigService,
			Map<String, Object> configMap, 
			String topicName,
			OpenNLPConfigService openNLPConfig) {
		
		this.sparkConfigService = sparkConfigService;
		this.configMap = configMap;
		this.topicName = topicName;
		this.openNLPConfig = openNLPConfig;
	}
	@Override
	public void run() {
		try {
			final SparkOpenNlpProcessor sparkOpenNlpService = new SparkOpenNlpProcessor();		
			new FileUploadConsumerTestTask(
					sparkConfigService.getSparkConfig(FileUploadContentConsumerService.class.getName()),
					this.configMap, this.topicName,sparkOpenNlpService,openNLPConfig.getSentenceModel());
			
		} catch (UnknownHostException | InterruptedException e) {
			logger.warn("", e);
		}
	}

}
