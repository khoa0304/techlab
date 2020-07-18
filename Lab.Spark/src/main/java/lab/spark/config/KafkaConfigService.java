package lab.spark.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfigService {

	@Value("${kafka.server.list}")
	private String kafkaServerList;

	@Value("${zookeeper.server.list}")
	private String zookeeperServerList;
	
	//@Value("${kafka.textfile.upload.topic:defaultvalue}")
	@Value("${kafka.textfile.upload.topic}")
	private String kafkaTextFileUploadTopic;
	

	public String getKafkaServerList() {
		return this.kafkaServerList;
	}
	
	public String getZookeeperServerList() {
		return this.zookeeperServerList;
	}
	
	public String getKafkaTextFileUploadTopic() {
		return this.kafkaTextFileUploadTopic;
	}
}

	