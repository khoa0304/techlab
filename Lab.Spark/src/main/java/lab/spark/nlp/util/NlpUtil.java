package lab.spark.nlp.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

public class NlpUtil {

	String name;
	private static final Logger LOGGER = LoggerFactory.getLogger(NlpUtil.class);
	
	public Set<String> getStopWordsSet(){

		String fileLocationPrefix = "classpath:stopwords/english/stopWords_%d.txt";
		
		Set<String> stopWordsSet =  new HashSet<>();
		
		for(int i = 1 ; i <= 3; i ++) {
			
			String fileLocation = String.format(fileLocationPrefix, i);
			try {
				
				File file = ResourceUtils.getFile(fileLocation);
				List<String> lines = Files.readAllLines(file.toPath());
				Set<String>  set = lines.stream().map(new Function<String, String>() {

					@Override
					public String apply(String t) {

						if(t != null && t.trim().length() > 0) {
							
							return t.toLowerCase();
						}
						return "";
					}
				}).collect(Collectors.toSet());;

				stopWordsSet.addAll(set);
			}catch (IOException e) {
				LOGGER.error("Error reading stop words ",e);
			}
		
		}
		
		
		return stopWordsSet;
	}
}
