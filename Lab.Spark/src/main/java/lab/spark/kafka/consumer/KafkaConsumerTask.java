package lab.spark.kafka.consumer;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import lab.spark.config.OpenNLPConfigService;
import lab.spark.config.SparkConfigService;

public class KafkaConsumerTask implements Runnable {

	private Logger logger = LoggerFactory.getLogger(KafkaConsumerTask.class);
	
	private SparkConfigService sparkConfigService;
	private  Map<String, Object> configMap;
	
	private String topicName;

	private String sparkStreamingSinkTopicList;
	private String sparkStreamingSinkSentenceCountTopic;
	private RestTemplate restTemplate;
	private String kafkaServiceName;
	
	public KafkaConsumerTask(
			SparkConfigService sparkConfigService,
			Map<String, Object> configMap, 
			String topicName,
			OpenNLPConfigService openNLPConfig,
			String sparkStreamingSinkTopicList,
			String sparkStreamingSinkSentenceCountTopic,
			RestTemplate restTemplate,
			String kafkaServiceName) {
		
		this.sparkConfigService = sparkConfigService;
		this.configMap = configMap;
		this.topicName = topicName;
		this.sparkStreamingSinkTopicList = sparkStreamingSinkTopicList;
		this.sparkStreamingSinkSentenceCountTopic = sparkStreamingSinkSentenceCountTopic;
		this.restTemplate = restTemplate;
		this.kafkaServiceName = kafkaServiceName;
		
	}
	@Override
	public void run() {
		try {
			
			String response = restTemplate.exchange(
					"http://"+ kafkaServiceName +"/kafka/config/topic/create?topicName="+sparkStreamingSinkTopicList,
					HttpMethod.POST, null,String.class).getBody();
			
	        logger.info("Response from Kafka " + response);
			
			//final SparkOpenNlpProcessor sparkOpenNlpService = new SparkOpenNlpProcessor();		
			WordCountKafkaStreamConsumer fileUploadConsumerTestTask = 
					new WordCountKafkaStreamConsumer();
			
			fileUploadConsumerTestTask.processFileUpload(
					sparkConfigService.getSparkConfig(FileUploadContentConsumerService.class.getName()), 
					configMap, topicName,sparkStreamingSinkTopicList);
			
			SentenceCountKafkaStreamConsumer sentenceCountKafkaStreamConsumer =
					new SentenceCountKafkaStreamConsumer();
			
			sentenceCountKafkaStreamConsumer.processFileUpload(
					sparkConfigService.getSparkConfig(FileUploadContentConsumerService.class.getName()), 
					configMap, topicName, sparkStreamingSinkSentenceCountTopic);
			
		} catch (Exception e) {
			logger.warn("", e);
		}
	}

}
