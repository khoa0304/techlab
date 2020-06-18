package lab.kafka.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

	@Value("${kafka.server.list:placeholder}")
	private String kafkaServerList;

	@Value("${zookeeper.server.list:placeholder}")
	private String zookeeperServerList;

	public String getKafkaServerList() {
		return this.kafkaServerList;
	}
	
	public String getZookeeperServerList() {
		return this.zookeeperServerList;
	}
}

	