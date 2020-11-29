package lab.spark.cassandra.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.datastax.spark.connector.ColumnRef;
import com.datastax.spark.connector.cql.TableDef;
import com.datastax.spark.connector.writer.RowWriter;
import com.datastax.spark.connector.writer.RowWriterFactory;

import lab.spark.dto.WordsPerSentenceDTO;
import scala.collection.IndexedSeq;
import scala.collection.Seq;

public class SentenceWordsWriter implements RowWriter<WordsPerSentenceDTO> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static RowWriter<WordsPerSentenceDTO> writer = new SentenceWordsWriter();

	// Factory
	public static class SentenceWordsWriterFactory implements RowWriterFactory<WordsPerSentenceDTO>, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public RowWriter<WordsPerSentenceDTO> rowWriter(TableDef arg0, IndexedSeq<ColumnRef> arg1) {
			return writer;
		}
	}

	@Override
	public Seq<String> columnNames() {

		List<String> columnNames = new ArrayList<>();
		columnNames.add("filename");
		columnNames.add("sentence");
		columnNames.add("wordarray");
		columnNames.add("totalstems");
		columnNames.add("sentencelength");
		
		return scala.collection.JavaConversions.asScalaBuffer(columnNames).toList();
	}

	@Override
	public void readColumnValues(WordsPerSentenceDTO wordsPerSentenceDTO, Object[] buffer) {
		buffer[0] = wordsPerSentenceDTO.getFileName();
		buffer[1] = wordsPerSentenceDTO.getSentence();
		buffer[2] = wordsPerSentenceDTO.getWords();
		buffer[3] = wordsPerSentenceDTO.getTotalStems();
		buffer[4] = wordsPerSentenceDTO.getSentenceLength();
	}

}
