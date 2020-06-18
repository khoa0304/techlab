package lab.kafka.config;

import java.util.Properties;

import org.apache.kafka.common.utils.Time;
import org.springframework.stereotype.Service;

import kafka.admin.RackAwareMode;
import kafka.zk.AdminZkClient;
import kafka.zk.KafkaZkClient;


@Service
public class TopicCreationService {

	
	public void createKafkaTopic(String zookeeperHost, String topicName,
			String metricGroup,String metricType) {
	
		Boolean isSucre = false;
		int sessionTimeoutMs = 200000;
		int connectionTimeoutMs = 150000;
		int maxInFlightRequests = 10;
		Time time = Time.SYSTEM;
	
		KafkaZkClient zkClient = KafkaZkClient.apply(
				zookeeperHost,isSucre,sessionTimeoutMs,
		        connectionTimeoutMs,maxInFlightRequests,
		        time,metricGroup,metricType, null);

		//ZkClient zkClient = new ZkClient(zookeeperHost, sessionTimeoutMs, connectionTimeoutMs);
		 
		AdminZkClient adminZkClient = new AdminZkClient(zkClient);
		int partitions = 3;
		int replication = 1;
		
		scala.collection.Map<String,Properties> topicNames = adminZkClient.getAllTopicConfigs();
		
		if(topicNames.contains(topicName)) {
			return;
		}
		Properties topicConfig = new Properties();
		adminZkClient.createTopic(topicName,partitions,replication,
		            topicConfig,RackAwareMode.Disabled$.MODULE$);
	}

}
