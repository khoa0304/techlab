package lab.cassandra.rest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/")
public class CassandraRestService {

	@Autowired
	private RestTemplate restTemplate;

	
	@GetMapping("/ping")
	@ResponseBody
	public String sayHello(@RequestParam(name = "name", required = false, defaultValue = "Stranger") String name) {
		return "Cassandra service responds " + name;
	}
	
	@GetMapping("/check")
	@ResponseBody
	public ResponseEntity<String> pingDataCapture(@RequestParam(name = "name", required = false, defaultValue = "From Data Capture") String name) {
		 return restTemplate
	                .exchange("http://datacapture-service/ping?name=" +  name,
	                		HttpMethod.GET, null,String.class);
	}
}
