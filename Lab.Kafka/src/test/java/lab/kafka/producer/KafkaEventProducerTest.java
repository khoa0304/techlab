package lab.kafka.producer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import lab.KafkaSpringBootApplication;
import lab.kafka.config.KafkaConfig;
import lab.kafka.config.TopicCreationService;
import lab.kafka.consumer.KafkaEventConsumer;

//@TestPropertySource(properties = {
//"some.bar.value=testValue",
//})
@SpringBootTest(classes = KafkaSpringBootApplication.class)
@TestPropertySource(locations = { "classpath:test.properties" })
public class KafkaEventProducerTest extends AbstractTestNGSpringContextTests {

	@Autowired
	private KafkaEventProducer kafkaEventProducer;

	@Autowired
	private KafkaConfig kafkaConfig;
	
	@Autowired
	private TopicCreationService topicCreationService;

	public static final String TOPIC_NAME = "Test-Topic";
	
	@Test
	public void testProducer1() throws InterruptedException, ExecutionException {

		String kafkaServerList = kafkaConfig.getKafkaServerList();

		Properties config = new Properties();
		config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerList);
		
		topicCreationService.createKafkaTopic(
				kafkaConfig.getZookeeperServerList(), TOPIC_NAME, TOPIC_NAME, TOPIC_NAME);
		
		KafkaEventConsumer kafkaEventConsumer = new KafkaEventConsumer();
		kafkaEventConsumer.createConsumer(kafkaServerList,TOPIC_NAME, TOPIC_NAME);
		
		ExecutorService scheduledExecutor = Executors.newSingleThreadExecutor();
		scheduledExecutor.submit(kafkaEventConsumer);
		
		Producer<String, String> producer = kafkaEventProducer.createProducer(kafkaServerList);
		long time = System.currentTimeMillis();

		try {
			for (long index = time; index < time + 10; index++) {
				final ProducerRecord<String, String> record = 
						new ProducerRecord<>(TOPIC_NAME, String.valueOf(index),"Hello World " + index);

				RecordMetadata metadata = producer.send(record).get();

				long elapsedTime = System.currentTimeMillis() - time;
				System.out.printf("sent record(key=%s value=%s) " + "meta(partition=%d, offset=%d) time=%d\n",
						record.key(), record.value(), metadata.partition(), metadata.offset(), elapsedTime);

			}
		} finally {
			
			kafkaEventConsumer.stopped();
			scheduledExecutor.shutdown();
			producer.flush();
			producer.close();
			
		}
	}

	@Test
	public void testReadFromFileUploadedTopic() {
		
		String kafkaServerList = kafkaConfig.getKafkaServerList();
		String topicName = kafkaConfig.getKafkaTextFileUploadTopic();

		Properties config = new Properties();
		config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerList);
		
		topicCreationService.createKafkaTopic(
				kafkaConfig.getZookeeperServerList(), topicName, topicName, topicName);
		
		KafkaEventConsumer kafkaEventConsumer = new KafkaEventConsumer();
		kafkaEventConsumer.createConsumer(kafkaServerList,topicName, topicName);
		
		ExecutorService scheduledExecutor = Executors.newSingleThreadExecutor();
		Future<Void> future = scheduledExecutor.submit(kafkaEventConsumer);
		try {
			future.get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("End");
	}
}
