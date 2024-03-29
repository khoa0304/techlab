package lab.spark.sample;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.storage.StorageLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.Test;

import lab.spark.config.CassandraConfigService;
import lab.spark.config.OpenNLPConfigService;
import lab.spark.dto.SentencesDTO;
import lab.spark.model.SparkOpenNlpProcessor;

@SpringBootTest
public class SparkCassandraTest extends CommonTestSetup {

	public static final String KEYSPACE = "lab";
	public static final String TABLE = "document_pdf";
	public static final String COLUMN_CONTENT = "content";
	
	@Autowired
	private CassandraConfigService cassandraConfig;
	
	@Autowired
	private OpenNLPConfigService openNLPConfig;
	
	@Test
	public void test1() throws IOException, ClassNotFoundException {
		
		SparkOpenNlpProcessor sparkOpenNlpService = new SparkOpenNlpProcessor();
		
		this.sparkSession = sparkConfigService.getSparkSessionForCassandra(getClass().getName(),true);
		
		SparkCassandra sparkCassandra = new SparkCassandra();
//		JavaRDD<String> stringRDD = sparkCassandra.perform(sparkSession, KEYSPACE, TABLE, COLUMN_CONTENT);
//		List<String> list = stringRDD.collect();
	
//		Dataset<Row> dataset2 = sparkCassandra.performDataFrame(sparkSession, KEYSPACE, TABLE, cassandraConfig.getClusterName());
//		dataset2.show(1,false);

		
//		Dataset<String[]> dataset3 = 
//				sparkCassandra.processContent(sparkSession, KEYSPACE, TABLE, cassandraConfig.getClusterName(),"UTResume.pdf.txt");
		
		Dataset<SentencesDTO> dataset3 = 
				sparkCassandra.processContent(
						sparkSession,sparkOpenNlpService, openNLPConfig,
						KEYSPACE, TABLE, cassandraConfig.getClusterName(),null);
		
		
		dataset3.persist(StorageLevel.MEMORY_ONLY());
		
		List<SentencesDTO> list = dataset3.collectAsList();
		
		for(SentencesDTO sentences : list) {
			System.out.println("\n\n=============================================");
			Arrays.stream(sentences.getSentences()).forEach(num -> System.out.println(num));
			System.out.println("=============================================");
		}

				
	}
	
	
	private class PrintPartion implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public void printDataset(Dataset<String[]> dataset) {
		
		
		}
	}
}
