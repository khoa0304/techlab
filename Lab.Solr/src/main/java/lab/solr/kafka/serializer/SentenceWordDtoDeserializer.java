package lab.solr.kafka.serializer;

import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import lab.common.file.dto.SentenceWordDto;

public class SentenceWordDtoDeserializer implements Deserializer<SentenceWordDto> {

	@Override
	public SentenceWordDto deserialize(String topic, byte[] data) {
		ObjectMapper mapper = new ObjectMapper();
		SentenceWordDto sentenceWordDto = null;
		try {
			sentenceWordDto = mapper.readValue(data, SentenceWordDto.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sentenceWordDto;
	}

}
