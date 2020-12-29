package lab.spark.kafka.consumer.segmentgroup;

public class SegmentGroupFactory {

	public enum SEGMENTGROUP{
		WORD,SENTENCECOUNT,SENTENCE
	}
	
	public static SegmentGroup<?> createSegmentGroup(SEGMENTGROUP segmentGroup) {
		
		switch(segmentGroup) {
		
			case WORD:
				StemWordSegmentGroup stemWordSegmentGroup = new StemWordSegmentGroup();
				return stemWordSegmentGroup;
				
			case SENTENCECOUNT:
				SentenceCountSegmentGroup sentenceCountSegmentGroup = new SentenceCountSegmentGroup();
				return sentenceCountSegmentGroup;
				
			case SENTENCE:
				SentenceSegmentGroup sentenceSegmentGroup = new SentenceSegmentGroup();
				return sentenceSegmentGroup;
		}
		return null;
	}
}
