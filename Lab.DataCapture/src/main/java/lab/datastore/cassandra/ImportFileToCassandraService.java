package lab.datastore.cassandra;

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
@RequestMapping("/datacapture")
public class ImportFileToCassandraService {

	@Autowired
	private RestTemplate restTemplate;

	@GetMapping("/text")
	@ResponseBody
	public ResponseEntity<String> sayHello(@RequestParam(name = "name", required = false, defaultValue = "Stranger") String name) {
		 return restTemplate
	                .exchange("http://cassandra-service/cassandra/rawtext?name=" +  name,
	                		HttpMethod.GET, null,String.class);
	}
	
	
	@GetMapping("/ping")
	@ResponseBody
	public String pong(@RequestParam(name = "name", required = false, defaultValue = "Stranger") String name) {
		return " Data Capture Service respond " + name;
	}
}