package lab.cassandra.repository.service;

import static com.datastax.driver.core.querybuilder.QueryBuilder.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;

import lab.cassandra.db.models.SentenceWords;

@Repository
public class SentenceWordsRepository {

	private Mapper<SentenceWords> mapper;
	private Session session;

	private static final String TABLE = SentenceWords.TABLE_NAME;
	
	public SentenceWordsRepository(MappingManager mappingManager) {		
		this.mapper = mappingManager.mapper(SentenceWords.class);
		this.session = mappingManager.getSession();
	}
	
	public SentenceWords find(String fileName) {
		return mapper.get(fileName);
	}

	public List<SentenceWords> findAll() {
		final ResultSet result = session.execute(select().all().from(TABLE));
		return mapper.map(result).all();
	}

	public List<SentenceWords> findAllByFileName(String fileName) {
		final ResultSet result = session
				.execute(select().all().from(TABLE)
						.where(
								eq(SentenceWords.COLUMNS.FILE_NAME.getColumnName(), fileName)
							   )
						.and(eq(SentenceWords.COLUMNS.TOTALWORDS.getColumnName(),2))
						.and(eq(SentenceWords.COLUMNS.SENTENCELENGTH.getColumnName(),10))
						
						);
		return mapper.map(result).all();
	}

	public void delete(String fileName) {
		mapper.delete(fileName);
	}

	public SentenceWords save(SentenceWords sentenceWords) {
		mapper.save(sentenceWords);
		return sentenceWords;
	}
}
