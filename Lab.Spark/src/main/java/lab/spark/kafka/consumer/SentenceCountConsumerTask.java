package lab.spark.kafka.consumer;

import java.util.Map;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import lab.spark.config.OpenNLPConfigService;
import lab.spark.config.SparkConfigService;

public class SentenceCountConsumerTask implements Runnable {

	private Logger logger = LoggerFactory.getLogger(SentenceCountConsumerTask.class);
	
	private SparkSession sparkSession; 
	private JavaStreamingContext jssc;
	private  Map<String, Object> configMap;
	
	private String topicName;

	private String sparkStreamingSinkSentenceCountTopic;
	private RestTemplate restTemplate;
	private String kafkaServiceName;
	
	public SentenceCountConsumerTask(
			SparkSession sparkSession , 
			JavaStreamingContext jssc,
			Map<String, Object> configMap, 
			String topicName,
			OpenNLPConfigService openNLPConfig,
			String sparkStreamingSinkSentenceCountTopic,
			RestTemplate restTemplate,
			String kafkaServiceName) {
		this.sparkSession = sparkSession;
		this.jssc = jssc;
		this.configMap = configMap;
		this.topicName = topicName;
		this.sparkStreamingSinkSentenceCountTopic = sparkStreamingSinkSentenceCountTopic;
		this.restTemplate = restTemplate;
		this.kafkaServiceName = kafkaServiceName;
		
	}
	@Override
	public void run() {
		try {
			
			String response = restTemplate.exchange(
					"http://"+ kafkaServiceName +"/kafka/config/topic/create?topicName="+sparkStreamingSinkSentenceCountTopic,
					HttpMethod.POST, null,String.class).getBody();
			
	        logger.info("Response from Kafka " + response);
			
			SentenceCountKafkaStreamConsumer sentenceCountKafkaStreamConsumer =
					new SentenceCountKafkaStreamConsumer();
			
			sentenceCountKafkaStreamConsumer.processFileUpload(
					sparkSession,jssc, 
					configMap, topicName, sparkStreamingSinkSentenceCountTopic);
			
		
		} catch (Exception e) {
			logger.warn("", e);
		}
	}

}
