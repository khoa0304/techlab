package lab.spark.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CassandraConfig {

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

	@Value("${spring.data.cassandra.cluster}")
	private String clusterName;

	public String getContactPoints() {
		return contactPoints;
	}

	public int getPort() {
		return port;
	}

	public String getKeySpace() {
		return keySpace;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getClusterName() {
		return clusterName;
	}
	
	
}

	