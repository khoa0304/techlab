package lab.kafka.rest.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lab.kafka.config.TopicCreationService;

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
	@ResponseBody
	public ResponseEntity<HttpStatus> createKafkaTopic(@RequestParam(name = "topicName", required = true) String topicName) {

		try {
	
			topicCreationService.createKafkaTopic(zookeeperServerList, topicName, topicName, topicName);
			logger.info("Kafka Topic {} is created successfully.", topicName);
		    return new ResponseEntity<HttpStatus>(HttpStatus.CREATED);
			
		} catch (Exception e) {
			logger.error("{}",e);
		}
		return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
	
	}
}
