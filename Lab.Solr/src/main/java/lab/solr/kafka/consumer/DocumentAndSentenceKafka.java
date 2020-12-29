package lab.solr.kafka.consumer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lab.common.file.dto.SentenceWordDto;

public class DocumentAndSentenceKafka extends CommonKafkaConsumerConfig implements Callable<Void> {

	private Logger logger = LoggerFactory.getLogger(DocumentAndSentenceKafka.class);

	private volatile boolean isStopped = false;

	private HttpSolrClient httpSolrClient;

	public DocumentAndSentenceKafka(HttpSolrClient solr) {
		this.httpSolrClient = solr;
	}

	@Override
	public Void call() throws Exception {

		while (!isStopped) {

			ConsumerRecords<String, SentenceWordDto> records = consumer.poll(Duration.ofSeconds(3));

			final List<SolrInputDocument> list = new ArrayList<SolrInputDocument>();
			
			for (ConsumerRecord<String, SentenceWordDto> record : records) {

				logger.debug("Sorl-Kafka-Consumer- Received - offset = {}, key = {}, value = {}", record.offset(),
						record.key(), record.value());

				SentenceWordDto sentenceWordDto = record.value();
				logger.info(sentenceWordDto.toString());
//				SolrInputDocument solrInputDocument = new SolrInputDocument();
//				solrInputDocument.addField("word", word);
//				solrInputDocument.addField("sentence", sentenceAndWordStem.getSentence());
//				solrInputDocument.addField("fileName", sentenceAndWordStem.getFileName());
//				list.add(solrInputDocument);
			}
			
			httpSolrClient.add(list);
			httpSolrClient.commit();

		}

		return null;

	}

	public void stopped() {
		this.isStopped = true;
	}

}
