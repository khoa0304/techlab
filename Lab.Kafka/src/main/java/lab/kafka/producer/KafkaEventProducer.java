package lab.kafka.producer;

import java.time.LocalDateTime;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lab.kafka.config.KafkaConfig;
import lab.kafka.config.TopicCreationService;
import lab.kafka.dto.FileContentDTO;


@Service
public class KafkaEventProducer {
	
	private Logger logger = LoggerFactory.getLogger(KafkaEventProducer.class);
	
	@Autowired
	private KafkaConfig kafkaConfig;
	
	@Autowired
	private TopicCreationService topicCreationService;

	public final static String TOPIC = "sample-topic";
	public final static String BOOTSTRAP_SERVERS =
			// "10.16.1.2:9092,localhost:9093,localhost:9094";
			"10.16.1.2:9092";


	private KafkaProducer<String, String> kafkaProducer;
	
	@PostConstruct
	public void setupProducer() {
		
		String topicGroupNameType = kafkaConfig.getKafkaTextFileUploadTopic();
		
		topicCreationService.createKafkaTopic(
				kafkaConfig.getZookeeperServerList(), topicGroupNameType, topicGroupNameType, topicGroupNameType);
		
		
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getKafkaServerList());
		props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaFileUploadProducer");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		
		Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
		
		kafkaProducer = new KafkaProducer<>(props);
	}
	
	@PreDestroy
	public void shutdown() {
		logger.info("Shutdow Kafka producer");
		kafkaProducer.flush();
		kafkaProducer.close();
	}
	
	public Producer<String, String> createProducer(String kafkaServer) {
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
		props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaFileUploadProducer");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
		return new KafkaProducer<>(props);
	}
	
	
	public void produceFileUploadContent(FileContentDTO fileContentDTO) {
	
		RecordMetadata metadata;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String message = objectMapper.writeValueAsString(fileContentDTO);
			
			final ProducerRecord<String, String> record = 
					new ProducerRecord<>(kafkaConfig.getKafkaTextFileUploadTopic(), LocalDateTime.now().toString(),message);
			
			metadata = kafkaProducer.send(record).get();
			
			logger.info("Sent record(key={} value={}) " + "meta(partition={}, offset={}) time={}",
					record.key(), record.value(), metadata.partition(), metadata.offset(),metadata.timestamp());
			
		} catch (InterruptedException | ExecutionException | JsonProcessingException e) {
			logger.error(""+e);
		}
	
	}
	
	
}
