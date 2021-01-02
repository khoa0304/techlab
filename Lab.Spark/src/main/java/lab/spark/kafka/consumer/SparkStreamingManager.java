package lab.spark.kafka.consumer;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import javax.annotation.PostConstruct;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lab.spark.config.KafkaConfigService;
import lab.spark.config.SparkConfigService;
import lab.spark.kafka.consumer.segmentgroup.SegmentGroupFactory.BUILTIN_SEGMENTGROUP;

@Service
public class SparkStreamingManager {

	private Logger logger = LoggerFactory.getLogger(SparkStreamingManager.class);

	@Autowired
	private SparkConfigService sparkConfigService;

	@Autowired
	private KafkaConfigService kafkaConfig;

//	@Autowired
//	private OpenNLPConfigService openNLPConfig;
	
	@Value("${kafka.service.name}")
	private String kafkaServiceName;

	@Autowired
	private RestTemplate restTemplate;
	
	
	@Value("${spark.stream.sink.wordcount.topic}")
	private String sparkStreamingSinkWordCountTopic;

	@Value("${spark.stream.sink.sentencecount.topic}")
	private String sparkStreamingSinkSentenceCountTopic;
	
	@Value("${spark.stream.sink.sentence.topic:SentenceTopic}")
	private String sparkStreamingSinkSentenceTopic;
	
	private JavaStreamingContext javaStreamingContext;
	private SparkSession sparkSession;
	private SparkConf sparkConfig;
	private SparkContext sparkContext;
	
	private ScheduledExecutorService scheduledExecutorService; 
	private SparkStreamingManagerTask sparkStreamingManagerTask;

	
	@PostConstruct
	private void initialize() {
		
	}

	public void startStreamingContext(Map<String, String> kafkaTopicPersegmentGroup, boolean localMode) {
		
		if( scheduledExecutorService != null && ! scheduledExecutorService.isTerminated()) return;
		// add default SEGMENTGROUP and topic
		kafkaTopicPersegmentGroup.put(BUILTIN_SEGMENTGROUP.WORD_COUNT.name(), sparkStreamingSinkWordCountTopic);
		kafkaTopicPersegmentGroup.put(BUILTIN_SEGMENTGROUP.SENTENCE_COUNT.name(), sparkStreamingSinkSentenceCountTopic);
		kafkaTopicPersegmentGroup.put(BUILTIN_SEGMENTGROUP.SENTENCE_AND_WORD_COUNT.name(), sparkStreamingSinkSentenceTopic);
		
		createSparkStreamingContext(localMode);
		startSparkKafkaStreaming(kafkaTopicPersegmentGroup);
	}
	
	public void stopStreamingContext() {
		stopStreamingContextInternal();
		if( scheduledExecutorService != null && ! scheduledExecutorService.isTerminated()) {
			scheduledExecutorService.shutdown();
		}
	}
	
	public void startSparkKafkaStreaming(Map<String, String> kafkaTopicPersegmentGroup) {

		String topicName = kafkaConfig.getKafkaTextFileUploadTopic();
		String kafkaServerList = kafkaConfig.getKafkaServerList();
		
		scheduledExecutorService = Executors.newScheduledThreadPool(1, new ThreadFactory() {

			@Override
			public Thread newThread(Runnable r) {
				Thread t = Executors.defaultThreadFactory().newThread(r);
				t.setDaemon(true);
				t.setName("Spark-Kafka-Streaming-Manager");
				return t;				
			}
		});
	
		sparkStreamingManagerTask = 
				new SparkStreamingManagerTask(	sparkSession,
													javaStreamingContext,
													restTemplate,
													kafkaServiceName,
													kafkaServerList,
													topicName,
													kafkaTopicPersegmentGroup);
		scheduledExecutorService.submit(sparkStreamingManagerTask);
		
		logger.info("Finished scheduling Spark-Kafka Consumer for streaming from topic {} ",topicName);

		
	}

	
	//~~~~~~~~~~~~~~ Private Implementation ~~~~~~~~~~~~~~~~~~~~~~~//


	
	private void createSparkStreamingContext(boolean localMode) {
		
		sparkConfig = sparkConfigService.getSparkConfig("Text-Document-Processing-App",localMode);
		sparkSession = SparkSession.builder().config(sparkConfig).getOrCreate();
		
		sparkContext = sparkSession.sparkContext();
		javaStreamingContext = 
				new JavaStreamingContext(JavaSparkContext.fromSparkContext(sparkContext), Durations.seconds(30));
		javaStreamingContext.checkpoint("./checkpoint/stream/jssc");
	}
	
	private boolean stopStreamingContextInternal() {
		
		try {
			if(javaStreamingContext == null) return false;
			javaStreamingContext.stop(true, true);
			sparkSession.stop();
			sparkContext.stop();
			
			return true;
			
		}catch (Exception e) {
			logger.warn(e.toString());
		}
		return false;
	}
}
