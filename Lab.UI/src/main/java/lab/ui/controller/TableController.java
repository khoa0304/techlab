package lab.ui.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lab.common.file.dto.DocumentStatisticDto;
import lab.ui.kafka.consumer.manager.UIKafkaConsumerGroupManager;
import lab.ui.model.WordCountDTO;

@Controller
@RequestMapping("/table")
public class TableController {

	@Autowired
	private UIKafkaConsumerGroupManager uiKafkaConsumerGroupManager;
	
	
	@GetMapping("/wordcount")
	@ResponseBody
	public List<WordCountDTO> wordCount() {
		return uiKafkaConsumerGroupManager.getWordCountKafkaConsumer().getWordCountList();
	}
	

	@GetMapping("/sentenceAndWordCount")
	@ResponseBody
	public List<DocumentStatisticDto> sentenceAndWordCount() {
		return uiKafkaConsumerGroupManager.getDocumentStatisticDTOList().getDocumentStatisticDTOList();
	}
}
