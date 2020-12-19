package lab.spark.kafka.consumer;

import java.util.Map;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import lab.spark.config.OpenNLPConfigService;

public class WordCountConsumerTask implements Runnable {

	private Logger logger = LoggerFactory.getLogger(WordCountConsumerTask.class);
	
	private  Map<String, Object> configMap;
	
	private String topicName;

	private String sparkStreamingSinkTopicList;
	
	private RestTemplate restTemplate;
	private String kafkaServiceName;
	
	private SparkSession sparkSession ;
	private JavaStreamingContext jssc;
	
	public WordCountConsumerTask(
			SparkSession sparkSession,
			JavaStreamingContext jssc,
			Map<String, Object> configMap, 
			String topicName,
			OpenNLPConfigService openNLPConfig,
			String sparkStreamingSinkTopicList,
	   	    RestTemplate restTemplate,
			String kafkaServiceName) {
		
		this.sparkSession = sparkSession;
		this.jssc = jssc;
		this.configMap = configMap;
		this.topicName = topicName;
		this.sparkStreamingSinkTopicList = sparkStreamingSinkTopicList;
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
					sparkSession,jssc, 
					configMap, topicName,sparkStreamingSinkTopicList);
		
			
		} catch (Exception e) {
			logger.warn("", e);
		}
	}

}
