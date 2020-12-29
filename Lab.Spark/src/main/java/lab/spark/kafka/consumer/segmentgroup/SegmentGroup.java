package lab.spark.kafka.consumer.segmentgroup;

import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import lab.spark.kafka.producer.KafkaProducerForSpark;

public interface SegmentGroup<T> {

    JavaDStream<T> streamTextContent(
			SparkSession sparkSession , 
			JavaStreamingContext javaStreamingContext,
			String kafkaServerList,
			String topicName,
			String sparkStreamingSinkTopicList);
    
    JavaDStream<T> streamTextContent(
			SparkSession sparkSession , 
			JavaStreamingContext javaStreamingContext,
			String kafkaServerList,
			String topicName,
			String sparkStreamingSinkTopicList,
			Broadcast<KafkaProducerForSpark> kafkaProducerBroadcast);
}
