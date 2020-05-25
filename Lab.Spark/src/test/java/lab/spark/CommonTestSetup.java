package lab.spark;

import java.net.UnknownHostException;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import lab.SparkSpringBootApplication;


@SpringBootTest(classes=SparkSpringBootApplication.class)
public class CommonTestSetup extends AbstractTestNGSpringContextTests {
	
	protected JavaSparkContext javaSparkContext;
	
	protected SparkSession sparkSession;
	
	@Autowired
	protected SparkConfigService sparkConfigService;
	
	@BeforeClass
	public void setup() throws UnknownHostException {
		if (javaSparkContext == null) {
			javaSparkContext = sparkConfigService.getJavaSparkContext();
		}
		if(sparkSession ==null) {
			sparkSession = sparkConfigService.getSparkSession(JoinRDDTest.class.getName());
		}
	}
	
	@AfterClass
	public void tearDown() {
		
		if(sparkSession ==null) {
			sparkSession.close();
		}
		
		if (javaSparkContext != null) {
			javaSparkContext.close();
		}
	
	}

}
