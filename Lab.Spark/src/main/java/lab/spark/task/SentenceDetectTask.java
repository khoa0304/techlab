package lab.spark.task;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lab.spark.dto.FileUploadContentDTO;
import lab.spark.dto.SentencesDTO;
import lab.spark.model.OpenNLPSerializedWrapper;
import opennlp.tools.sentdetect.SentenceModel;

public class SentenceDetectTask implements Serializable {

	private Logger logger = LoggerFactory.getLogger(SentenceDetectTask.class);

	private static final long serialVersionUID = 1L;

	final OpenNLPSerializedWrapper openNLPSerializedWrapper = OpenNLPSerializedWrapper.getInstance();

	public SentencesDTO extractSentencesFromContent(SentenceModel sentenceModel,
			FileUploadContentDTO fileUploadContentDTO) {

		String fileContent = fileUploadContentDTO.getFileContent();
		String[] sentences = openNLPSerializedWrapper.detectSentence(sentenceModel, fileContent);
		SentencesDTO sentencesDTO = new SentencesDTO(fileUploadContentDTO.getFileName(), sentences);
		logger.info("File Name {} - Sentences {}", fileUploadContentDTO.getFileName(),sentences.length);
		return sentencesDTO;
	}

}
