package lab.ui.controller;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.annotation.PostConstruct;
import lab.ui.file.service.FileStorageService;
import lab.ui.model.DocumentDto;
import lab.ui.model.UploadFileResponse;

@RestController
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

   
//    @Value("${datacapture.service.name}")
//	private String dataCaptureServiceName;
//    
//    @Value("${cassandra.service.name}")
//	private String cassandraServiceName;
	
    @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired
	private RestTemplate restTemplate;
    
    @PostMapping("/uploadFile")
    public ResponseEntity uploadFile(@RequestParam("photo") MultipartFile file,
    		@RequestParam("longitude") double longitude,
    		@RequestParam("latitude") double latitude) {
    	
    	logger.info("Finished uploading file {} - Size {} KB", file.getOriginalFilename(),file.getSize()/1024);
    	logger.info("longitude: {} - latitude: {}",longitude,latitude);
    	
        File uploadedFile = fileStorageService.storeFile(file);
        String fileName = uploadedFile.getAbsolutePath();
        
        logger.info("Finished storing file {} in {} ", file.getOriginalFilename(),fileName);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        DocumentDto documentDto = new DocumentDto();
        documentDto.setAbsoluteDirectoryPath(uploadedFile.getParentFile().getAbsolutePath());
        documentDto.setFileNamePlusExtesion(uploadedFile.getName());
        
//        restTemplate.postForEntity("http://"+dataCaptureServiceName+"/pdf/store",
//        		documentDto ,String.class);
        
//        return new UploadFileResponse(fileName, fileDownloadUri,
//                file.getContentType(), file.getSize());
        return  ResponseEntity.ok().build(); 
    }

//    @PostMapping("/uploadMultipleFiles")
//    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
//        return Arrays.asList(files)
//                .stream()
//                .map(file -> uploadFile(file))
//                .collect(Collectors.toList());
//    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
