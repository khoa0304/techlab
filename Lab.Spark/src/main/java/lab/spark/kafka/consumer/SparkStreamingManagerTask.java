package lab.spark.kafka.consumer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.kafka.clients.producer.Producer;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import lab.spark.kafka.consumer.segmentgroup.SegmentGroup;
import lab.spark.kafka.consumer.segmentgroup.SegmentGroupFactory;
import lab.spark.kafka.consumer.segmentgroup.SegmentGroupFactory.SEGMENTGROUP;
import lab.spark.kafka.producer.KafkaProducerForSpark;

public class SparkStreamingManagerTask implements Callable<Boolean>{

	private Logger logger = LoggerFactory.getLogger(SparkStreamingManagerTask.class);
	
	private String kafkaServiceName;
	private String topicName;

	private RestTemplate restTemplate;
	
	private JavaStreamingContext javaStreamingContext;
	private SparkSession sparkSession;
	
	private String kafkaServerList;
	private Map<SEGMENTGROUP, String> kafkaTopicPersegmentGroup = new HashMap<>();
	
	private Map<SEGMENTGROUP, KafkaProducerForSpark> kafkaProducerPersegmentGroup = new HashMap<>();
	  
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
			
			KafkaProducerForSpark kafkaProducerForSpark = new KafkaProducerForSpark(kafkaServerList);
			Broadcast<KafkaProducerForSpark> kafkaProducerBroadcast = sparkSession.sparkContext()
					.broadcast(kafkaProducerForSpark, scala.reflect.ClassTag$.MODULE$.apply(KafkaProducerForSpark.class));
			
			String sinkKafkaTopic = entry.getValue();
			SEGMENTGROUP segmentGroup = entry.getKey();
			String response = restTemplate.exchange(
					"http://"+ kafkaServiceName +"/kafka/config/topic/create?topicName="+sinkKafkaTopic,
					HttpMethod.POST, null,String.class).getBody();
			
	        logger.info("Response from Kafka " + response);
	        
	      
	        SegmentGroup<?> segmentGroupImpl = SegmentGroupFactory.createSegmentGroup(segmentGroup);
	        kafkaProducerPersegmentGroup.put(segmentGroup, kafkaProducerForSpark);
	        segmentGroupImpl.streamTextContent(sparkSession, javaStreamingContext, kafkaServerList, topicName, sinkKafkaTopic,kafkaProducerBroadcast);
	        
		}
		
		try {
			javaStreamingContext.start();
			javaStreamingContext.awaitTermination();
			
		}catch (Exception e) {
			logger.warn(e.toString());
		}
		finally {
			for(Map.Entry<SEGMENTGROUP, KafkaProducerForSpark> entry : kafkaProducerPersegmentGroup.entrySet()) {
				entry.getValue().shutdownProducer();
			}
		}
		
		return true;
	}	
}
