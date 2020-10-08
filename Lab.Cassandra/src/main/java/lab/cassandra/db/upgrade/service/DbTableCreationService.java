package lab.cassandra.db.upgrade.service;

import static com.datastax.driver.core.DataType.bigint;
import static com.datastax.driver.core.DataType.list;
import static com.datastax.driver.core.DataType.text;
import static com.datastax.driver.core.DataType.uuid;
import static com.datastax.driver.core.DataType.varchar;
import static com.datastax.driver.core.DataType.cint;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Service;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.schemabuilder.SchemaBuilder;
import com.datastax.driver.mapping.DefaultNamingStrategy;
import com.datastax.driver.mapping.DefaultPropertyMapper;
import com.datastax.driver.mapping.MappingConfiguration;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.NamingConventions;
import com.datastax.driver.mapping.PropertyMapper;

import lab.cassandra.db.models.DocumentPdf;
import lab.cassandra.db.models.SentenceWords;
@Service
public class DbTableCreationService {

	@Autowired
	private CassandraConfig cassandraConfig;

	@Autowired
	private CassandraOperations cassandraTemplate;
	
	@Autowired
	private Cluster cluster;
	
	@Autowired
	private Session session;

//	@Bean
//	public Cluster cluster() {
//
//		return Cluster.builder().addContactPoint(cassandraConfig.getContactPoints()).withPort(cassandraConfig.getPort())
//				.withClusterName(cassandraConfig.getClusterName()).build();
//	}

//	@Bean
//	public Session session(Cluster cluster) {
//		final Session session = cluster.connect();
//		return session;
//	}
	
	@Bean
	public MappingManager mappingManager(Session session) {
		
		final PropertyMapper propertyMapper = new DefaultPropertyMapper()
				.setNamingStrategy(
						new DefaultNamingStrategy(NamingConventions.LOWER_CAMEL_CASE, NamingConventions.LOWER_SNAKE_CASE));
		final MappingConfiguration configuration = MappingConfiguration.builder().withPropertyMapper(propertyMapper)
				.build();
		return new MappingManager(session, configuration);
	}

	@PostConstruct
	public void setupDB() {
		setupKeyspace(session, cassandraConfig.getKeyspaceName());
		createDocumentPdfTable(session);
		createSentenceWordsTable(session);
	}

	private void setupKeyspace(Session session, String keyspace) {

		final Map<String, Object> replication = new HashMap<>();
		replication.put("class", "SimpleStrategy");
		replication.put("replication_factor", 1);
		session.execute(SchemaBuilder.createKeyspace(keyspace).ifNotExists().with().replication(replication));
		session.execute("USE " + keyspace);
		// String[] statements =
		// split(IOUtils.toString(getClass().getResourceAsStream("/cql/setup.cql")),
		// ";");
		// Arrays.stream(statements).map(statement -> normalizeSpace(statement) +
		// ";").forEach(session::execute);
	}

	

	private void createDocumentPdfTable(Session session) {

		// @formatter off
		session.execute(SchemaBuilder.createTable(DocumentPdf.TABLE_NAME)
				.ifNotExists()
				.addPartitionKey(DocumentPdf.COLUMNS.FILE_NAME.getColumnName(), varchar())
				.addClusteringColumn(DocumentPdf.COLUMNS.SIZE.getColumnName(), bigint())
				.addColumn(DocumentPdf.COLUMNS.FILE_CONTENT.getColumnName(), text())
				.addColumn(DocumentPdf.COLUMNS.UUID.getColumnName(), uuid())
				);
		// .addColumn("age", cint()).addColumn("profession", text())
		// .addColumn("salary", cint()));

		// @formatter on
	}

	private void createSentenceWordsTable(Session session) {

		// @formatter off
		session.execute(SchemaBuilder.createTable(SentenceWords.TABLE_NAME)
				.ifNotExists()
				.addPartitionKey(SentenceWords.COLUMNS.FILE_NAME.getColumnName(), varchar())
				.addPartitionKey(SentenceWords.COLUMNS.TOTALWORDS.getColumnName(), cint())
				.addPartitionKey(SentenceWords.COLUMNS.SENTENCELENGTH.getColumnName(), cint())
				.addColumn(SentenceWords.COLUMNS.SENTENCE.getColumnName(), text())
				.addColumn(SentenceWords.COLUMNS.WORD_ARRAY.getColumnName(),list(DataType.text()))
				);


		// @formatter on
	}
}
