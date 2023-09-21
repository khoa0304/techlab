package lab.backend.persistence.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
public class MongoDbConfig extends AbstractMongoClientConfiguration {

	@Value("${spring.data.mongodb.custom.uri}") 
	private String uri;
	
	@Override
	public String getDatabaseName() {
		return "FeedLocation";
	}

	@Override
	public MongoClient mongoClient() {
		ConnectionString connectionString = new ConnectionString(uri);
		MongoClientSettings mongoClientSettings = 
				MongoClientSettings.builder().applyConnectionString(connectionString)
				.build();

		return MongoClients.create(mongoClientSettings);
	}
	
	public @Bean MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }
}
