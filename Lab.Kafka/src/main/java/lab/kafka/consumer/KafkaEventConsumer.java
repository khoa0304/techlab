package lab.kafka.consumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaEventConsumer implements Callable<Void>{
	
	private Logger logger = LoggerFactory.getLogger(KafkaEventConsumer.class); 

	private KafkaConsumer<String, String> consumer;
	private volatile boolean isStopped = false;
	
	public KafkaConsumer<String,String> createConsumer(
			String brokerServerList, String topicName, String groupName) {

		Properties props = new Properties();
		props.put("bootstrap.servers", brokerServerList);
		props.put("group.id", groupName);
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");
		props.put("session.timeout.ms", "30000");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		consumer = new KafkaConsumer<String, String>(props);

		consumer.subscribe(Arrays.asList(topicName));
		logger.info("Subscribed to topic " + topicName);
	
		return consumer;
		
	}

	@Override
	public Void call() throws Exception {
		
		while (!isStopped) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMinutes(1));
			for (ConsumerRecord<String, String> record : records)
				System.out.printf("offset = %d, key = %s, value = %s\n", record.offset(), record.key(), record.value());
		}
		
		return null;
	
	}
	
	public void stopped() {
		this.isStopped = true;
	}
}
