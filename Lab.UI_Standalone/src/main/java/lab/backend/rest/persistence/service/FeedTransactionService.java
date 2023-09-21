package lab.backend.rest.persistence.service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties.IsolationLevel;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import lab.ui.feeds.model.FeedsAskModel;

@Service
public class FeedTransactionService {

	@Autowired
	@Qualifier(value = "jdbcTemplatePostgresDataSource1")
	private JdbcTemplate jdbcTemplate;

	@Transactional(propagation = Propagation.REQUIRED)
	public long insertAskFeed(FeedsAskModel feedsAskModel) {

		final String query = "INSERT INTO ConsumerFeeds (feed_content,category) VALUES (?, ?) returning feed_id";

		try {

			KeyHolder keyHolder = new GeneratedKeyHolder();

			jdbcTemplate.update(connection -> {

				PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, feedsAskModel.getFeedContent());
				ps.setString(2, feedsAskModel.getCategory());
				return ps;
			}, keyHolder);

			return keyHolder.getKey().longValue();
		} catch (Exception e) {

			throw e;
		}

	}

//	public long insertAskFeedList(List<FeedsAskModel> feedsAskModelList) {
//		
//		final String query = "INSERT INTO ConsumerFeeds (feed_content,category) VALUES (?, ?) returning feed_id";
//        return transactionTemplate.(status -> {
//            try {
//            	
//            	KeyHolder keyHolder = new GeneratedKeyHolder();
//            	 
//            	jdbcTemplate.update(connection -> {
//            		
//            		PreparedStatement ps = connection.prepareStatement(query, 
//                            Statement.RETURN_GENERATED_KEYS);
//            		ps.setString(1,feedsAskModel.getFeedContent());
//            		ps.setString(2,feedsAskModel.getCategory());
//            		return ps;
//            	},keyHolder);
//                        
//               
//            	return keyHolder.getKey().longValue();
//            } catch (Exception e) {
//                status.setRollbackOnly(); 
//                throw e;
//            }
//        });
//    }

}