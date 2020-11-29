package lab.spark.sample;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;

import lab.SparkSpringBootApplication;
import lab.spark.config.SparkConfigService;


@SpringBootTest(classes=SparkSpringBootApplication.class)
public class CommonTestSetup extends AbstractTestNGSpringContextTests {
	
	protected JavaSparkContext javaSparkContext = null;
	
	protected SparkSession sparkSession = null;
	
	@Autowired
	protected SparkConfigService sparkConfigService;
	
	
	@AfterClass
	public void tearDown() {
		
		if(sparkSession !=null) {
			sparkSession.close();
		}
		
		if (javaSparkContext != null) {
			javaSparkContext.close();
		}
	
	}

}
