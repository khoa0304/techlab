package lab.spark.kafka.consumer;

import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.collections.map.HashedMap;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import lab.spark.config.OpenNLPConfigService;
import lab.spark.kafka.consumer.segmentgroup.SegmentGroup;
import lab.spark.kafka.consumer.segmentgroup.SegmentGroupFactory;
import lab.spark.kafka.consumer.segmentgroup.SegmentGroupFactory.SEGMENTGROUP;

public class SparkStreamingManagerTask implements Callable<Boolean>{

	private Logger logger = LoggerFactory.getLogger(SparkStreamingManagerTask.class);
	
	private String kafkaServiceName;
	private String topicName;

	private RestTemplate restTemplate;
	
	private JavaStreamingContext javaStreamingContext;
	private SparkSession sparkSession;
	
	private String kafkaServerList;
	private Map<SEGMENTGROUP, String> kafkaTopicPersegmentGroup = new HashedMap();
	
	public SparkStreamingManagerTask(
			SparkSession sparkSession,
			JavaStreamingContext jssc,
			RestTemplate restTemplate,
			String kafkaServiceName,
			String kafkaServerList,
			String topicName,
			Map<SEGMENTGROUP, String> kafkaTopicPersegmentGroup) {
		
		this.sparkSession = sparkSession;
		this.javaStreamingContext = jssc;
		this.topicName = topicName;
		this.restTemplate = restTemplate;
		this.kafkaServiceName = kafkaServiceName;
		this.kafkaServerList = kafkaServerList;
		this.kafkaTopicPersegmentGroup = kafkaTopicPersegmentGroup;
		
	}
	@Override
	public Boolean call() throws Exception {
	
		for(Map.Entry<SEGMENTGROUP, String> entry : kafkaTopicPersegmentGroup.entrySet()) {
			
			String sinkKafkaTopic = entry.getValue();
			SEGMENTGROUP segmentGroup = entry.getKey();
			String response = restTemplate.exchange(
					"http://"+ kafkaServiceName +"/kafka/config/topic/create?topicName="+sinkKafkaTopic,
					HttpMethod.POST, null,String.class).getBody();
			
	        logger.info("Response from Kafka " + response);
	        
	        SegmentGroup<?> segmentGroupImpl = SegmentGroupFactory.createSegmentGroup(segmentGroup);
	        segmentGroupImpl.streamTextContent(sparkSession, javaStreamingContext, kafkaServerList, topicName, sinkKafkaTopic);
	        
		}
		
		try {
			javaStreamingContext.start();
			javaStreamingContext.awaitTermination();
			
		}catch (Exception e) {
			logger.warn(e.toString());
		}
		
		return true;
	}	
}
