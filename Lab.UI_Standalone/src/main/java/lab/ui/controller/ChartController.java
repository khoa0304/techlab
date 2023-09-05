package lab.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import lab.ui.model.LabelAndCount;

@Controller
@RequestMapping("/chart")
public class ChartController {


	@GetMapping("/ping")
	@ResponseBody
	public String ping(@RequestParam(name = "name", required = false, defaultValue = "Hello UI Service") String name) {
		return "UI Chart Controller Service responds " + name;
	}	
    
    @GetMapping({"/chart1"})
    public String chart1(Model model) {
    	System.out.print(model);
        return "chart1";
    }
    
    @GetMapping({"/wordanalysis"})
    public String chart2(Model model) {
    	return "SentenceWordCount";
    }


//    @GetMapping({"/getTopWordCount"})
//    public  @ResponseBody String getTopWordCount(Model model) {
//    	LabelAndCount wordAndCount = 
//    			uiKafkaConsumerGroupManager.getWordCountKafkaConsumer().getWordAndCount();
//    	String json = new Gson().toJson(wordAndCount);
//    	
//        return json;
//    }
    
//    @GetMapping({"/getTotalSentenceAndWordCount"})
//    public  @ResponseBody String getTotalSentenceAndWordCount(Model model) {
//    	LabelAndCount wordAndCount = 
//    			uiKafkaConsumerGroupManager.getSentenceAndTotalWordCountKafkaConsumer().getWordAndCount();
//    	String json = new Gson().toJson(wordAndCount);
//    	
//        return json;
//    }
}