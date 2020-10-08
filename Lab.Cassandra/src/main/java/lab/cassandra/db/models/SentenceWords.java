package lab.cassandra.db.models;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;

import com.datastax.driver.core.DataType.Name;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

@Table(name = SentenceWords.TABLE_NAME)
public class SentenceWords implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "sentencewords";
	
	
	public static final String FILENAME_COL = "fileName";
	public static final String SENTENCE_COL = "sentence";
	public static final String TOTALSTEMS_COL = "totalStems";
	public static final String SENTENCELENGTH_COL = "sentenceLength";
	public static final String WORD_ARRAY_COL = "wordArray";
	
	public SentenceWords() {}
	
	public static enum COLUMNS {
		
		FILE_NAME(FILENAME_COL),
		SENTENCE(SENTENCE_COL), 
		TOTALWORDS(TOTALSTEMS_COL),
		WORD_ARRAY(WORD_ARRAY_COL),
		SENTENCELENGTH(SENTENCELENGTH_COL);
		
		private String columnName;
		
		private COLUMNS(String columnName) {
			this.columnName = columnName;
		}
		
		public String getColumnName() {
			return this.columnName;
		}
	}

	@Column(name=FILENAME_COL)
	@PrimaryKey
	@PartitionKey(0)
	@CassandraType(type=Name.VARCHAR)
	private String fileName;
	
	@Column(name = SENTENCE_COL)
	@CassandraType(type=Name.TEXT)
	private String sentence;


	@Column(name = WORD_ARRAY_COL)
	//@ClusteringColumn
	@CassandraType(type=Name.LIST.TEXT)
	private List<String> words;

	@Column(name = TOTALSTEMS_COL)
	@PrimaryKey
	@PartitionKey(1)
	@CassandraType(type=Name.INT)
	private int totalStems;
	
	@Column(name = SENTENCELENGTH_COL)
	@PrimaryKey
	@PartitionKey(2)
	@CassandraType(type=Name.INT)
	private int sentenceLength;
	
	// all other column will be used as regular columns

	
	public SentenceWords(String fileName, String sentence, List<String> words) {
		this.fileName = fileName;
		this.sentence = sentence;
		this.words = words;
		this.totalStems = words.size();
		this.sentenceLength = sentence.length();
	}
    
	public String getFileName() {
		return fileName;
	}	

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSentence() {
		return sentence;
	}


	public void setSentence(String sentence) {
		this.sentence = sentence;
	}


	public List<String> getWords() {
		return words;
	}

	public void setWords(List<String> words) {
		this.words = words;
	}

	public int getTotalStems() {
		return totalStems;
	}

	public void setTotalStems(int totalStems) {
		this.totalStems = totalStems;
	}
	
	
	public int getSentenceLength() {
		return sentenceLength;
	}

	public void setSentenceLength(int sentenceLength) {
		this.sentenceLength = sentenceLength;
	}

	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + ((sentence == null) ? 0 : sentence.hashCode());
		result = prime * result + sentenceLength;
		result = prime * result + totalStems;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SentenceWords other = (SentenceWords) obj;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (sentence == null) {
			if (other.sentence != null)
				return false;
		} else if (!sentence.equals(other.sentence))
			return false;
		if (sentenceLength != other.sentenceLength)
			return false;
		if (totalStems != other.totalStems)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DocumentPdf [fileName=" + fileName + ", sentence=" + sentence + "]";
	}
}
