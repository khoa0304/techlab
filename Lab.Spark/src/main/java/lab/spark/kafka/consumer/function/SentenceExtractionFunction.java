package lab.spark.kafka.consumer.function;

import java.io.File;
import java.io.Serializable;

import org.apache.spark.api.java.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lab.spark.dto.FileUploadContentDTO;
import lab.spark.dto.SentencesDTO;
import lab.spark.task.SentenceDetectTask;
import opennlp.tools.sentdetect.SentenceModel;

public class SentenceExtractionFunction implements Serializable, Function<FileUploadContentDTO, SentencesDTO> {

	private Logger logger = LoggerFactory.getLogger(SentenceExtractionFunction.class);
	
	private static final long serialVersionUID = 1L;
	private SentenceModel sentenceModel = null;


	@Override
	public SentencesDTO call(FileUploadContentDTO fileUploadContentDTO) throws Exception {
		
		if (sentenceModel == null) {
			sentenceModel = new SentenceModel(new File("/opt/spark-data/opennlp/models/en-sent.bin"));
			logger.info("Initialize Sentence Model");
		}

		SentenceDetectTask sentenceDetectTask = new SentenceDetectTask();
		SentencesDTO sentencesDTO = sentenceDetectTask.extractSentencesFromContent(sentenceModel,
				fileUploadContentDTO);

		return sentencesDTO;
	}

}
