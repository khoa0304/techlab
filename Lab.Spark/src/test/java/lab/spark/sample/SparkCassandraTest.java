package lab.spark.sample;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.Test;

import lab.spark.config.CassandraConfig;

@SpringBootTest
public class SparkCassandraTest extends CommonTestSetup {

	public static final String KEYSPACE = "lab";
	public static final String TABLE = "document_pdf";
	public static final String COLUMN_CONTENT = "content";
	
	@Autowired
	private CassandraConfig cassandraConfig;
	
	@Test
	public void test1() {
		
		this.sparkSession = sparkConfigService.getSparkSessionForCassandra(getClass().getName());
		
		SparkCassandra sparkCassandra = new SparkCassandra();
//		JavaRDD<String> stringRDD = sparkCassandra.perform(sparkSession, KEYSPACE, TABLE, COLUMN_CONTENT);
//		List<String> list = stringRDD.collect();
	
		Dataset<Row> dataset2 = sparkCassandra.performDataFrame(sparkSession, KEYSPACE, TABLE, cassandraConfig.getClusterName());
		dataset2.show(1,false);
//		Dataset<Row> dataset3 = sparkCassandra.performSparkSQLUsingView1(sparkSession, KEYSPACE, TABLE, cassandraConfig.getClusterName(),COLUMN_CONTENT);
//		dataset3.show();
		
	}
	
	
}
