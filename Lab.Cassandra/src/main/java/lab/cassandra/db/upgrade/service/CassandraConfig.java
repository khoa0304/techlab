package lab.cassandra.db.upgrade.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;

import com.datastax.driver.core.AuthProvider;
import com.datastax.driver.core.PlainTextAuthProvider;

@Configuration
public class CassandraConfig extends AbstractCassandraConfiguration {

	@Value("${spring.data.cassandra.contact-points:placeholder}")
	private String contactPoints;

	@Value("${spring.data.cassandra.port:0000}")
	private int port;

	@Value("${spring.data.cassandra.keyspace:placeholder}")
	private String keySpace;

	@Value("${spring.data.cassandra.username}")
	private String username;

	@Value("${spring.data.cassandra.password}")
	private String password;

	@Value("${spring.data.cassandra.schema-action}")
	private String schemaAction;
	
	@Value("${spring.data.cassandra.cluster}")
	private String clusterName;
	

	@Override
	protected String getKeyspaceName() {
		return keySpace;
	}

	@Override
	protected String getContactPoints() {
		return contactPoints;
	}

	@Override
	protected int getPort() {
		return port;
	}

	@Override
	public SchemaAction getSchemaAction() {
		return SchemaAction.valueOf(schemaAction);
	}

	@Override
	protected AuthProvider getAuthProvider() {
		return new PlainTextAuthProvider(username, password);
	}
	
	@Override
	public String getClusterName() {
		return this.clusterName;
	}
}