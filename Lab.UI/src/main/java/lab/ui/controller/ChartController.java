package lab.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import lab.ui.model.TagAndCount;
import lab.ui.model.WordsPerSentenceDTO;

@Controller
@RequestMapping("/chart")
public class ChartController {

    @GetMapping
    public String main() {
        return "index";
    }
    
    @GetMapping({"/chart1"})
    public String chart1(Model model) {
    	System.out.print(model);
        return "chart1";
    }
    
    @GetMapping({"/chart2"})
    public String chart2(Model model) {
    	return "chart2";
    }
    
    @PostMapping({"/updateData"})
    public String updateData(@RequestBody WordsPerSentenceDTO wordsPerSentenceDTO) {
    	return "chart2";
    }
    
	
	@GetMapping("/ping")
	@ResponseBody
	public String ping(@RequestParam(name = "name", required = false, defaultValue = "Hello UI Service") String name) {
		return "UI Chart Controller Service responds " + name;
	}	
    
    
    private long counter = 0;
    
    @GetMapping({"/refreshData"})
    public  @ResponseBody String refreshData(Model model) {
    	System.out.print(model);
    	
    	TagAndCount tagAndCount1 = new TagAndCount(new String[]{""},new long[] {counter});
    	String json = new Gson().toJson(tagAndCount1);
    	
        return json;
    }
}