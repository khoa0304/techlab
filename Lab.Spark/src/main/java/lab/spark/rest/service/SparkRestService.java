package lab.spark.rest.service;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

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
	
	private final AtomicBoolean isSparkStreamingStarted = new AtomicBoolean(false);
	
	@GetMapping("/ping")
	@ResponseBody
	public String ping(@RequestParam(name = "name", required = false, defaultValue = "Stranger") String name) {
		logger.info("Spark Rest Service got ping.");
		return " Spark Service responds " + name;
	}	
	
	@GetMapping("/streaming/stop")
	@ResponseBody
	public String stopStreaming() {
		
		if(!isSparkStreamingStarted.get()) {
			return  "Spark Streaming Service is already stopped";
		}
		logger.info("Stop streaming context");
		sparkStreamingManager.stopStreamingContext();
		isSparkStreamingStarted.set(false);
		return " Spark Streaming Service Stopped";
	}
	
	@GetMapping("/streaming/start")
	@ResponseBody
	public String startStreaming(
			@RequestParam(name = "localMode", required = false, defaultValue = "false") boolean localMode) {
		
		if(isSparkStreamingStarted.get()) {
			return  "Spark Streaming Service is already started";
		}
		logger.info("Start streaming context");
		sparkStreamingManager.startStreamingContext(new HashMap<>(0),localMode);
		isSparkStreamingStarted.set(true);
		return " Spark Streaming Service Started";
	}

}
