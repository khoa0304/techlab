package lab.backend.persistence.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;
import org.springframework.jdbc.core.JdbcTemplate;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
/**
 * https://www.jackrutorial.com/2018/08/multiple-datasource-in-spring-boot.html
 */
@Configuration
public class PostgresDbConfig {

	@Bean(name = "postgresDataSource1Properties")
	@Primary
	@ConfigurationProperties(prefix = "spring.postgres-1")
	public DataSourceProperties dataSourceProperties() {
	    return new DataSourceProperties();
	}
	
	@Bean(name = "postgresDataSource1")
	@ConfigurationProperties(prefix = "spring.postgres-1")
	public DataSource postgresDataSource1(@Qualifier("postgresDataSource1Properties") DataSourceProperties properties) {
		//DataSource ds = DataSourceBuilder.create().build();
		return properties.initializeDataSourceBuilder().build();
	}

	@Bean(name = "jdbcTemplatePostgresDataSource1")
	public JdbcTemplate jdbcTemplate1(@Qualifier("postgresDataSource1") DataSource ds) {
		return new JdbcTemplate(ds);
	}
	
	
//	@Bean(name = "db2")
//	@ConfigurationProperties(prefix = "spring.second-db")
//	public DataSource dataSource2() {
//		return DataSourceBuilder.create().build();
//	}
//
//	@Bean(name = "jdbcTemplate2")
//	public JdbcTemplate jdbcTemplate2(@Qualifier("db2") DataSource ds) {
//		return new JdbcTemplate(ds);
//	}
}
