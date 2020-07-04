package lab.spark.rest.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spark")
public class SparkRestService {

	private Logger logger = LoggerFactory.getLogger(SparkRestService.class);
	
		
	@GetMapping("/ping")
	@ResponseBody
	public String ping(@RequestParam(name = "name", required = false, defaultValue = "Stranger") String name) {
		logger.info("Spark Rest Service got ping.");
		return " Kafka Service responds " + name;
	}	
	

}
