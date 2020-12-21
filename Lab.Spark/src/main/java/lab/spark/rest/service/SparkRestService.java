package lab.spark.rest.service;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lab.spark.kafka.consumer.SparkStreamingManager;

@RestController
@RequestMapping("/spark")
public class SparkRestService {

	private Logger logger = LoggerFactory.getLogger(SparkRestService.class);
	
	@Autowired
	private SparkStreamingManager sparkStreamingManager;
	
	@GetMapping("/ping")
	@ResponseBody
	public String ping(@RequestParam(name = "name", required = false, defaultValue = "Stranger") String name) {
		logger.info("Spark Rest Service got ping.");
		return " Spark Service responds " + name;
	}	
	
	@GetMapping("/streaming/stop")
	@ResponseBody
	public String stopStreaming() {
		logger.info("Stop streaming context");
		sparkStreamingManager.stopStreamingContext();
		return " Spark Streaming Service Stopped";
	}
	
	@GetMapping("/streaming/start")
	@ResponseBody
	public String startStreaming() {
		logger.info("Start streaming context");
		sparkStreamingManager.startStreamingContext(new HashMap<>(0));
		return " Spark Streaming Service Started";
	}

}
