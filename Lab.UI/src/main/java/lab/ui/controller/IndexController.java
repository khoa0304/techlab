package lab.ui.controller;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/")
public class IndexController {
	
	
	@Value("${spark.service.name:spark-service}")
	private String sparkServiceName;

	@Autowired
	private RestTemplate restTemplate;

	@GetMapping("/")
    public String main() {
        return "index";
    }
	
	final AtomicBoolean isSparkStreamingStarted = new AtomicBoolean(false);
	
	@PostMapping("/streaming/startstop")
	@ResponseBody
	public String startStopSparkStreamingService(@RequestParam(name = "name", required = false, defaultValue = "name") String name) {
		
		String response = "";
		if(isSparkStreamingStarted.get()) {
			
			response = restTemplate.exchange(
					"http://"+ sparkServiceName +"/spark/streaming/stop",
					HttpMethod.GET, null,String.class).getBody();
			
			isSparkStreamingStarted.set(false);
		}
		else{
	
			response = restTemplate.exchange(
					"http://"+ sparkServiceName +"/spark/streaming/start",
					HttpMethod.GET, null,String.class).getBody();
			
			isSparkStreamingStarted.set(true);
		}
		
		return response;
	}
	

	
}