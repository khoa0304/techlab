package lab.spark.sample;

import java.net.UnknownHostException;

import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.Test;

import lab.spark.sample.ReduceAndGroupByKey;

@SpringBootTest
public class ReduceAndGroupByKeyTest extends CommonTestSetup {

	@Test
	public void test1() throws UnknownHostException  {
		 
		this.javaSparkContext = sparkConfigService.getJavaSparkContext(getClass().getName());
		ReduceAndGroupByKey reduceAndGroupByKey = new ReduceAndGroupByKey();
		reduceAndGroupByKey.perform(this.javaSparkContext);
		
	}
}
