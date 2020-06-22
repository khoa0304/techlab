package lab.kafka.consumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaEventConsumer implements Callable<Void>{
	
	private Logger logger = LoggerFactory.getLogger(KafkaEventConsumer.class); 

	private KafkaConsumer<Long, String> consumer;
	private volatile boolean isStopped = false;
	
	public KafkaConsumer<Long,String> createConsumer(
			String brokerServerList, String topicName, String groupName) {

		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerServerList);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupName);
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.LongDeserializer");
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
		consumer = new KafkaConsumer<Long, String>(props);

		consumer.subscribe(Arrays.asList(topicName));
		logger.info("Subscribed to topic " + topicName);
	
		return consumer;
		
	}

	@Override
	public Void call() throws Exception {
		
		while (!isStopped) {
			ConsumerRecords<Long, String> records = consumer.poll(Duration.ofMinutes(1));
			for (ConsumerRecord<Long, String> record : records)
				System.out.printf("offset = %d, key = %s, value = %s\n", record.offset(), record.key(), record.value());
		}
		
		return null;
	
	}
	
	public void stopped() {
		this.isStopped = true;
	}
}
