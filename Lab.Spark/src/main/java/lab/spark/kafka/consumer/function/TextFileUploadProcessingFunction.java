package lab.spark.kafka.consumer.function;

import java.io.Serializable;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.api.java.function.Function;

import com.fasterxml.jackson.databind.ObjectMapper;

import lab.spark.dto.FileUploadContentDTO;

public class TextFileUploadProcessingFunction implements Serializable, Function<ConsumerRecord<String, String>, FileUploadContentDTO> {

	
	private static final long serialVersionUID = 1L;

	@Override
	public FileUploadContentDTO call(ConsumerRecord<String, String> kafkaRecord) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		FileUploadContentDTO fileUploadContent = objectMapper.readValue(kafkaRecord.value(),
				FileUploadContentDTO.class);
		return fileUploadContent;
	}

}
