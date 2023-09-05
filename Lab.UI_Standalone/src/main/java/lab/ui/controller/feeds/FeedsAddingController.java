package lab.ui.controller.feeds;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lab.ui.feeds.model.FeedsAskModel;

//@CrossOrigin(origins = "http://localhost:8080") 
@RestController
@RequestMapping("/feeds")
public class FeedsAddingController {

	

    @PostMapping("/add")
    public ResponseEntity<String> addTask(@RequestBody FeedsAskModel ask) {
    	
    	System.out.println(ask);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
