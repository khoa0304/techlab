package lab.spark.kafka.consumer.segmentgroup;

public class SegmentGroupFactory {

	public static enum BUILTIN_SEGMENTGROUP {
		WORD_COUNT,SENTENCE_COUNT,SENTENCE_AND_WORD_COUNT
	}
	
	public static SegmentGroup<?> createSegmentGroup(String segmentGroup) {
		
		switch(segmentGroup) {
		
			case "WORD_COUNT":
				StemWordSegmentGroup stemWordSegmentGroup = new StemWordSegmentGroup();
				return stemWordSegmentGroup;
				
			// This segment group sent to Solr for indexing
			case "SENTENCE_COUNT":
				SentenceSegmentGroup sentenceSegmentGroup = new SentenceSegmentGroup();
				return sentenceSegmentGroup;
				
			case "SENTENCE_AND_WORD_COUNT":
				TotalSentenceAndWordPerDocument sentenceCountSegmentGroup = new TotalSentenceAndWordPerDocument();
				return sentenceCountSegmentGroup;
		}
		return null;
	}
}
