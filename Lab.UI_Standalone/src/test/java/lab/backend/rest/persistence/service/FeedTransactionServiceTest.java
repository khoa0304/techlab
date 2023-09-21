package lab.backend.rest.persistence.service;

import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

import java.util.Arrays;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import com.mongodb.client.result.InsertOneResult;

import lab.backend.persistence.config.MongoDbConfig;
import lab.ui.feeds.model.FeedsAskModel;

@SpringBootTest
public class FeedTransactionServiceTest {

	@Autowired
	FeedTransactionService feedTransactionService;
	
	//https://www.codementor.io/@prasadsaya/access-mongodb-database-from-a-spring-boot-application-17nwi5shuc
	@Autowired
	MongoDbConfig mongoDbConfig;
	
	@Test
	public void test1() {
		FeedsAskModel feedsAskModel = new FeedsAskModel(
				System.currentTimeMillis()+"1", 
				System.currentTimeMillis()+"1", 
				System.currentTimeMillis()+"1",
				System.currentTimeMillis()+"1" ,
				System.currentTimeMillis()+"1");
	    long feedId = feedTransactionService.insertAskFeed(feedsAskModel);
		
		
		// Get the database you want to work with
		MongoDatabase database = mongoDbConfig.mongoClient().getDatabase("FeedLocation");
		
		MongoCollection<Document> collection = database.getCollection("locations");

		collection.createIndex(Indexes.geo2dsphere("location"));
	
		Document locationDocument = 
				new Document()
				.append("feed_id", feedId)
				.append("location", new Document()
				.append("type", "Point")
				.append("coordinates", Arrays.asList(-122.458169, 37.693383)));


		
		try {
			collection.deleteOne(locationDocument);
			InsertOneResult result = collection.insertOne(locationDocument);
			System.out.println("Insert document Id: " + result.getInsertedId());
			
		} catch (Exception e) {
			System.out.println(e);
		}

		Point searchPoint = new Point(new Position(-122.458083,37.693394));
		Bson query2 = Filters.nearSphere("location", searchPoint, 78660.0, 1.0);
		Bson projection = fields(include("location"), excludeId());
		
		// Execute the query
		FindIterable<Document> results = collection.find(query2).projection(projection);
		
		// Process the results
		for (Document document : results) {
			System.out.println(document);
		}
	}
	
}
