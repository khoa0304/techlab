package lab.solr.rest.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

import lab.solr.rest.pojo.SentenceAndStem;

@Controller
@RequestMapping("/solr")
public class SearchController {

	private Logger logger = LoggerFactory.getLogger(SearchController.class);
	
	@Value("${solr.server.endpoint}")
	private String solrServerEndpoint;
	
	@Value("${solr.collection.default}")
	private String defaultCollection;
	
	private HttpSolrClient solr;
	
	@PostConstruct
	public void initSolrClient() {
		solr = new HttpSolrClient.Builder(solrServerEndpoint+defaultCollection).build();
		solr.setParser(new XMLResponseParser());
	}
	
	@GetMapping("/ping")
	@ResponseBody
	public String ping(@RequestParam(name = "name", required = false, defaultValue = "Stranger") String name) {
		return " Solr Search Service responds. Solr Endpoint " + solrServerEndpoint +" Default Collection " + defaultCollection;
	}	
	
    
    @PostMapping(path = "/sentence/index",consumes= MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<HttpStatus> indexSentence(@RequestBody SentenceAndStem sentenceAndWordStem) {

		try {
			int numberOfWords = sentenceAndWordStem.getWords().length;
			if(numberOfWords == 0) {
				return new ResponseEntity<HttpStatus>(HttpStatus.LENGTH_REQUIRED); 
			}
		
			
			final List<SolrInputDocument> list = new ArrayList<SolrInputDocument>();
			
			for(String word : sentenceAndWordStem.getWords()) {
		
				SolrInputDocument solrInputDocument = new SolrInputDocument();
				solrInputDocument.addField("word", word);
				solrInputDocument.addField("sentence", sentenceAndWordStem.getSentence());
				solrInputDocument.addField("fileName", sentenceAndWordStem.getFileName());
				list.add(solrInputDocument);
			}
		
			solr.add(list);
			solr.commit();
		   
			return new ResponseEntity<HttpStatus>(HttpStatus.OK);
			
		} catch (Exception e) {
			logger.error("{}",e);
		}
		
		return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
	
	}
}