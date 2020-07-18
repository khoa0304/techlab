package lab.cassandra.repository.service;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;

import lab.cassandra.db.models.DocumentPdf;

@Repository
public class DocumentPdfRepository {

	private Mapper<DocumentPdf> mapper;
	private Session session;

	private static final String TABLE = DocumentPdf.TABLE_NAME;
	
	public DocumentPdfRepository(MappingManager mappingManager) {		
		this.mapper = mappingManager.mapper(DocumentPdf.class);
		this.session = mappingManager.getSession();
	}
	
	public DocumentPdf find(String fileName) {
		return mapper.get(fileName);
	}

	public List<DocumentPdf> findAll() {
		final ResultSet result = session.execute(select().all().from(TABLE));
		return mapper.map(result).all();
	}

	public List<DocumentPdf> findAllByFileName(String fileName) {
		final ResultSet result = session
				.execute(select().all().from(TABLE).where(eq(DocumentPdf.COLUMNS.FILE_NAME.getColumnName(), fileName)));
		return mapper.map(result).all();
	}

	public void delete(String fileName) {
		mapper.delete(fileName);
	}

	public DocumentPdf save(DocumentPdf documentPdf) {
		mapper.save(documentPdf);
		return documentPdf;
	}
}
