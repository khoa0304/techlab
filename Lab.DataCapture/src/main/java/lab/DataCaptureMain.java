package lab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import lab.datacapture.file.service.FileStorageProperties;


@SpringBootApplication
@EnableConfigurationProperties({
    FileStorageProperties.class
})
public class DataCaptureMain {

	public static void main(String[] args) {
		SpringApplication.run(DataCaptureMain.class, args);
	}
	
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

	
}
