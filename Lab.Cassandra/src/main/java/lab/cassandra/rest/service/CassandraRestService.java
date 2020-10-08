package lab.cassandra.rest.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;

import lab.cassandra.db.models.DocumentPdf;
import lab.cassandra.repository.service.DocumentPdfRepository;
import lab.cassandra.repository.service.SentenceWordsRepository;
import lab.common.file.dto.DocumentDto;
import lab.common.file.handler.FileResourceUtil;

@Controller
@RequestMapping("/")
public class CassandraRestService {

	private Logger logger = LoggerFactory.getLogger(CassandraRestService.class);

	@Autowired
	private RestTemplate restTemplate;

	@Value("${datacapture.service.name}")
	private String dataCaptureServiceName;

	@Autowired
	private EurekaClient eurekaClient;

	@Autowired
	private DocumentPdfRepository documentPdfRepository;
	
	@Autowired
	private SentenceWordsRepository sentenceWordsRepository;
	
	@Autowired
	private FileResourceUtil fileResourceUtil;
	

	@GetMapping("/ping")
	@ResponseBody
	public String sayHello(@RequestParam(name = "name", required = false, defaultValue = "Stranger") String name) {
		return "Cassandra service responds " + name;
	}

	@GetMapping("/check")
	@ResponseBody
	public ResponseEntity<String> pingDataCapture(
			@RequestParam(name = "name", required = false, defaultValue = "From Cassandra") String name) {

		Application application = eurekaClient.getApplication(dataCaptureServiceName);
		InstanceInfo instanceInfo = application.getInstances().get(0);
		logger.info(instanceInfo.toString());

		return restTemplate.exchange("http://" + dataCaptureServiceName + "/ping?name=" + name, HttpMethod.GET, null,
				String.class);
	}

	@PostMapping(path = "/save",consumes= MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<HttpStatus> savePdfToCassandra(@RequestBody DocumentDto documentDto) {

		FileInputStream stream = null;
		
		try {
			
			final File file = new File(documentDto.getAbsoluteFilePath());
			
		    stream = new FileInputStream(file);
		    final FileChannel fc = stream.getChannel();
		    final String content = fileResourceUtil.readFileContentAsText(documentDto);
		    
		    DocumentPdf documentPdf = new DocumentPdf(documentDto.getFileNamePlusExtesion(), content, fc.size());
		    documentPdfRepository.save(documentPdf);
		   
			return new ResponseEntity<HttpStatus>(HttpStatus.ACCEPTED);
			
		} catch (IOException e) {
			logger.error("{}",e);
		}
		
		finally{
			
			if(stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					logger.error("{}",e);
				}
			}
		}
		
		return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);

	}

}
