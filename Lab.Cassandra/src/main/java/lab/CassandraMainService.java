package lab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class CassandraMainService {

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

//	@Autowired
//	private RestService restService;
//
//	@Bean
//	public RestTemplate restTemplate() {
//		return restService.getRestTemplate();
//	}

	public static void main(String[] args) {
		SpringApplication.run(CassandraMainService.class, args);
	}
}
