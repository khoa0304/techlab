package lab.spark.kafka.producer;


import java.io.Serializable;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.hadoop.util.Time;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class KafkaProducerForSpark implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public final Map<String, Producer<String,String>> PRODUCER_MAP = new ConcurrentHashMap<>();

	private String kafkaServerList;
	
	public KafkaProducerForSpark(String kafkaServerList) {
		this.kafkaServerList = kafkaServerList;
	}
	public Producer<String, String> initKafkaProducer() {
		
		if(PRODUCER_MAP.containsKey(kafkaServerList)) {
			return PRODUCER_MAP.get(kafkaServerList);
		}
		
		Properties props = new Properties();
		props.put("bootstrap.servers", kafkaServerList);
//		props.put("ack", "all");
//		props.put("retries", 5);
		props.put("batch-size", 10);
//		props.put("linger.ms", 5);
//		props.put("buffer.memeory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		KafkaProducer<String, String>  producer = new KafkaProducer<>(props);
		PRODUCER_MAP.put(kafkaServerList, producer);
		return producer;
	}

	public void pushToKafka(Producer<String, String> producer, String topic, Integer partition, String key, String value) {
		producer.send(new ProducerRecord<String, String>(topic, partition, Time.now(), key, value));
	}

	public void pushToKafka(Producer<String, String> producer, String topic, String value) {
		producer.send(new ProducerRecord<String, String>(topic, value));
	}
	
	public void shutdownProducer() {
		
		for(Map.Entry<String, Producer<String,String>> entry : PRODUCER_MAP.entrySet()) {
			entry.getValue().close();
		}
		
		PRODUCER_MAP.clear();
	}
	
}
