package lab.ui.controller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

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

import lab.ui.kafka.consumer.SentenceAndTotalWordCountKafkaConsumer;
import lab.ui.kafka.consumer.WordCountKafkaConsumer;
import lab.ui.model.LabelAndCount;
import lab.ui.model.WordsPerSentenceDTO;

@Controller
@RequestMapping("/chart")
public class ChartController {

	@Value("${kafka.server.list}")
	private String kafkaServerList;
	
	@Value("${spark.stream.sink.wordcount.topic}")
	private String sparkStreamingSinkWordCountTopic;
	
	@Value("${spark.stream.sink.sentencecount.topic}")
	private String sparkStreamingSinkSentenceCountTopic;
	
	private WordCountKafkaConsumer wordCountKafkaConsumer;
	private SentenceAndTotalWordCountKafkaConsumer sentenceAndTotalWordCountKafkaConsumer;
	
	@PostConstruct
	public void startExecutorService() {
		
	    wordCountKafkaConsumer = new WordCountKafkaConsumer();
		wordCountKafkaConsumer.createStringKeyValueConsumer(kafkaServerList,sparkStreamingSinkWordCountTopic, "UI-WordCount-Consumer-Group");
		
		sentenceAndTotalWordCountKafkaConsumer = new SentenceAndTotalWordCountKafkaConsumer();
		sentenceAndTotalWordCountKafkaConsumer.createStringKeyValueConsumer(kafkaServerList, sparkStreamingSinkSentenceCountTopic, "UI-SentenceCount-Consumer-Group");
		
		ExecutorService scheduledExecutor = Executors.newFixedThreadPool(2);
		scheduledExecutor.submit(wordCountKafkaConsumer);
		scheduledExecutor.submit(sentenceAndTotalWordCountKafkaConsumer);
		
	}
	
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


    @GetMapping({"/getTopWordCount"})
    public  @ResponseBody String getTopWordCount(Model model) {
    	LabelAndCount wordAndCount = wordCountKafkaConsumer.getWordAndCount();
    	String json = new Gson().toJson(wordAndCount);
    	
        return json;
    }
    
    @GetMapping({"/getTotalSentenceAndWordCount"})
    public  @ResponseBody String getTotalSentenceAndWordCount(Model model) {
    	LabelAndCount wordAndCount = sentenceAndTotalWordCountKafkaConsumer.getWordAndCount();
    	String json = new Gson().toJson(wordAndCount);
    	
        return json;
    }
}