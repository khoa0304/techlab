package lab.spark.kafka.consumer.segmentgroup;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

public interface SegmentGroup<T> {

	public JavaDStream<T> streamTextContent(
			SparkSession sparkSession , 
			JavaStreamingContext javaStreamingContext,
			String kafkaServerList,
			String topicName,
			String sparkStreamingSinkTopicList);
}
