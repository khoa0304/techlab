package lab.ui.controller.feeds;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lab.ui.model.DealFeed;
import lab.ui.model.MyDataDTO;

@CrossOrigin(origins = "http://localhost:8080") 
@RestController
@RequestMapping("/feeds/listing")
public class FeedsListingController {

	private static final Logger logger = LoggerFactory.getLogger(FeedsListingController.class);

	@RequestMapping(value = "/all", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	public List<DealFeed> getNewsFeed(@RequestBody  MyDataDTO data) {
		System.out.println(data);
		// Simulate fetching news articles from a database or external API
		List<DealFeed> newsFeed = new ArrayList<>();
		newsFeed.add(new DealFeed("This is feed 1", "https://wallpaperset.com/w/full/d/2/b/115638.jpg"));
		newsFeed.add(new DealFeed("This is feed 2", "https://wallpaperset.com/w/full/6/6/4/115664.jpg"));
		newsFeed.add(new DealFeed("This is feed 3", "https://wallpaperset.com/w/full/9/8/4/115675.jpg"));
		
		return newsFeed;
	}
}
