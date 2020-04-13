package lab.datastore.cassandra;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/datacapture")
public class LoadFileToCassandra {

	  @GetMapping("/hello-world")
	  @ResponseBody
	  public String sayHello(@RequestParam(name="name", required=false, defaultValue="Stranger") String name) {
	    return "Hello "+ name;
	  }
	
}