//package lab.rest.service;
//
//import org.springframework.boot.web.client.RestTemplateBuilder;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import lab.security.WebSecurity;
//
//@Service
//public class RestService {
//
//    private final RestTemplate restTemplate;
//
//    public RestService(RestTemplateBuilder restTemplateBuilder) {
//        this.restTemplate = restTemplateBuilder
//                .basicAuthentication(WebSecurity.USER_NAME,WebSecurity.PASSWORD)
//                .build();
//    }
//    
//    public RestTemplate getRestTemplate() {
//    	return this.restTemplate;
//    }
//}