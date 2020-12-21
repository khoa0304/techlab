package lab.spark.kafka.consumer.segmentgroup;

public class SegmentGroupFactory {

	public enum SEGMENTGROUP{
		WORD,SENTENCE
	}
	
	public static SegmentGroup<?> createSegmentGroup(SEGMENTGROUP segmentGroup) {
		
		switch(segmentGroup) {
		
			case WORD:
				StemWordSegmentGroup stemWordSegmentGroup = new StemWordSegmentGroup();
				return stemWordSegmentGroup;
				
			case SENTENCE:
				SentenceSegmentGroup sentenceCountKafkaStreamConsumer = new SentenceSegmentGroup();
				return sentenceCountKafkaStreamConsumer;
		}
		return null;
	}
}
