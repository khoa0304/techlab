package lab.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/")
public class IndexController {
	
	@Autowired
	private RestTemplate restTemplate;

	@GetMapping("/")
    public String main() {
        return "DisplayFeeds1";
    }
	
	@GetMapping("/ConsumerAskForm")
    public String consumerAskForm() {
        return "ConsumerAskForm";
    }
	
}