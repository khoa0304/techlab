package lab.kafka.rest.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lab.common.file.dto.DocumentDto;
import lab.common.file.handler.FileResourceUtil;
import lab.kafka.dto.FileContentDTO;
import lab.kafka.producer.KafkaEventProducer;

@RestController
@RequestMapping("/kafka/producer")
public class FileUploadRestService {

	private Logger logger = LoggerFactory.getLogger(FileUploadRestService.class);
	
	@Autowired
	private FileResourceUtil fileResourceUtil;
	
	@Autowired
	private KafkaEventProducer kafkaEventProducer;
	
	@PostMapping(path = "/fileUpload",consumes= MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<HttpStatus> savePdfToCassandra(@RequestBody DocumentDto documentDto) {

		try {
			
		    final String content = fileResourceUtil.readFileContentAsText(documentDto);
		    
		    FileContentDTO fileContentDTO = new FileContentDTO();
		    fileContentDTO.setFileName(documentDto.getFileNamePlusExtesion());
		    fileContentDTO.setFileContent(content);
		
		    kafkaEventProducer.produceFileUploadContent(fileContentDTO);
		   
			return new ResponseEntity<HttpStatus>(HttpStatus.OK);
			
		} catch (IOException e) {
			logger.error("{}",e);
		}
		
		return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
	
	}
}
