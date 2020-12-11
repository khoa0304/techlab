package lab.ui.controller;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import lab.ui.kafka.consumer.WordCountKafkaConsumer;
import lab.ui.model.WordAndCount;
import lab.ui.model.WordsPerSentenceDTO;

@Controller
@RequestMapping("/chart")
public class ChartController {

	@Value("${kafka.server.list}")
	private String kafkaServerList;
	
	@Value("${spark.stream.sink.wordcount.topic}")
	private String sparkStreamingSinkWordCountTopic;
	
	private WordCountKafkaConsumer kafkaEventConsumer;
	
	@PostConstruct
	public void startExecutorService() {
		
	    kafkaEventConsumer = new WordCountKafkaConsumer();
		kafkaEventConsumer.createConsumer(kafkaServerList,sparkStreamingSinkWordCountTopic, "UI-WordCount-Consumer-Group");
		
		ExecutorService scheduledExecutor = Executors.newSingleThreadExecutor();
		scheduledExecutor.submit(kafkaEventConsumer);
	}
	
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
  
    @GetMapping({"/refreshData"})
    public  @ResponseBody String refreshData(Model model) {
    	WordAndCount wordAndCount = kafkaEventConsumer.getWordAndCount();
    	String json = new Gson().toJson(wordAndCount);
    	
        return json;
    }
}