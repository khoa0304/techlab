package lab.datacapture.rest.service;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
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

import lab.common.file.dto.DocumentDto;
import lab.common.file.handler.FileResourceUtil;
import lab.datacapture.pdf.reader.service.SimplePdfReader;

@Controller
@RequestMapping("/")
public class DataCaptureRestService {
	
	private static final Logger logger = LoggerFactory.getLogger(DataCaptureRestService.class);

	@Autowired
	private SimplePdfReader simplePdfReader;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${cassandra.service.name}")
	private String cassandraServiceName;

	@Autowired
	private FileResourceUtil fileResourceUtil;
	
	@GetMapping("/check")
	@ResponseBody
	public ResponseEntity<String> check(@RequestParam(name = "name", required = false, defaultValue = "From Data Capture") String name) {
		 return restTemplate
	                .exchange("http://"+cassandraServiceName+"/ping?name=" +  name,
	                		HttpMethod.GET, null,String.class);
	}
	
	
	@GetMapping("/ping")
	@ResponseBody
	public String ping(@RequestParam(name = "name", required = false, defaultValue = "Stranger") String name) {
		return " Data Capture Service responds " + name;
	}	
	
	
	@PostMapping(path = "/pdf/store",consumes= MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<HttpStatus> parsePdf(@RequestBody DocumentDto documentDto) {
	
		RandomAccessFile randomAccessFile = null;
		FileChannel rwChannel = null;
		
		try {
			
			final File file = new File(documentDto.getAbsoluteFilePath());
			
			String content = simplePdfReader.extractPdfContent(file.toURI().toURL());
			logger.info("Content length {} ", content.length());
			
			File newTextFile = fileResourceUtil.writeFileContentAsText(documentDto, content);
			
			DocumentDto textFileDto = new DocumentDto();
			textFileDto.setAbsoluteDirectoryPath(newTextFile.getParentFile().getAbsolutePath());
			textFileDto.setFileNamePlusExtesion(newTextFile.getName());
			
			restTemplate.postForEntity("http://"+cassandraServiceName+"/save",
					textFileDto ,String.class);
		    
			return new ResponseEntity<HttpStatus>(HttpStatus.ACCEPTED);
			
		} catch (IOException e) {
			logger.error("{}",e);
			return new ResponseEntity<HttpStatus>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		finally{
		
			if(randomAccessFile != null) {
				try {
					randomAccessFile.close();
					
					if(rwChannel != null) {
						try {
							rwChannel.close();
						} catch (IOException e) {
							logger.error(e.toString());
						}
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}
	
	
}