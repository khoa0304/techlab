package lab.cassandra.rest.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;

@Controller
@RequestMapping("/")
public class CassandraRestService {

	private Logger logger = LoggerFactory.getLogger(CassandraRestService.class);
	
	@Autowired
	private RestTemplate restTemplate;

	@Value("${datacapture.service.name}")
	private String dataCaptureServiceName;

	@Autowired
	private EurekaClient eurekaClient;

	@GetMapping("/ping")
	@ResponseBody
	public String sayHello(@RequestParam(name = "name", required = false, defaultValue = "Stranger") String name) {
		return "Cassandra service responds " + name;
	}

	@GetMapping("/check")
	@ResponseBody
	public ResponseEntity<String> pingDataCapture(
			@RequestParam(name = "name", required = false, defaultValue = "From Cassandra") String name) {

		Application application = eurekaClient.getApplication(dataCaptureServiceName);
		InstanceInfo instanceInfo = application.getInstances().get(0);
		logger.info(instanceInfo.toString());
		
		return restTemplate.exchange("http://" + dataCaptureServiceName + "/ping?name=" + name, HttpMethod.GET, null,
				String.class);
	}
}
