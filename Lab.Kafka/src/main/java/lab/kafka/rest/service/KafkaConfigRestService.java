package lab.kafka.rest.service;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lab.kafka.config.TopicCreationService;
import scala.Option;
import scala.collection.Set;
import scala.collection.immutable.HashSet;

@RestController
@RequestMapping("/kafka/config")
public class KafkaConfigRestService {

	private Logger logger = LoggerFactory.getLogger(KafkaConfigRestService.class);

	@Autowired
	private TopicCreationService topicCreationService;
	
	@Value("${zookeeper.server.list}")
	private String zookeeperServerList;
	
	@GetMapping("/ping")
	@ResponseBody
	public String ping(@RequestParam(name = "name", required = false, defaultValue = "Stranger") String name) {
		return " Kafka Service responds " + name;
	}	
	
	
	@PostMapping(path = "/topic/create",consumes= MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Option<Properties>> createKafkaTopic(@RequestParam(name = "topicName", required = true) String topicName) {

		try {
	
			Option<Properties> topicProperties = 
					topicCreationService.createKafkaTopic(zookeeperServerList, topicName, topicName, topicName);
			logger.info("Kafka Topic {} is created successfully.", topicName);
		    return ResponseEntity.accepted().body(topicProperties);
			
		} catch (Exception e) {
			logger.error("{}",e);
		}
		return ResponseEntity.badRequest().build();
	
	}
	
	@GetMapping(path = "/topic/list",consumes= MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public boolean listKafkaTopic(@RequestParam(name = "topicName", required = true) String topicName) {

		try {
	
			Set<String> topicSet = topicCreationService.getAvailableTopics(zookeeperServerList, topicName, topicName, topicName);
			logger.info("Numer of Kafka Topic avaialble is {}.", topicSet.size());
			boolean isTopExisted = topicSet.contains(topicName);
			
		    return isTopExisted;
			
		} catch (Exception e) {
			logger.error("{}",e);
		}
		return false;
	
	}
}
