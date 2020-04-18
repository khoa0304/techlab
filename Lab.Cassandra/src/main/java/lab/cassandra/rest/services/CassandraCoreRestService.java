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
@RequestMapping("/cassandra")
public class CassandraCoreRestService {

	@Autowired
	private RestTemplate restTemplate;

	
	@GetMapping("/rawtext")
	@ResponseBody
	public String sayHello(@RequestParam(name = "name", required = false, defaultValue = "Stranger") String name) {
		return "Hello " + name;
	}
	
	@GetMapping("/ping")
	@ResponseBody
	public ResponseEntity<String> pingDataCapture(@RequestParam(name = "name", required = false, defaultValue = "Stranger") String name) {
		 return restTemplate
	                .exchange("http://datacapture-service/datacapture/ping?name=" +  name,
	                		HttpMethod.GET, null,String.class);
	}
}
