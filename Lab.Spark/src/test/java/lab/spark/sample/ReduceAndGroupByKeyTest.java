package lab.spark.sample;

import java.net.UnknownHostException;

import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.Test;

@SpringBootTest
public class ReduceAndGroupByKeyTest extends CommonTestSetup {

	@Test
	public void test1() throws UnknownHostException  {
		 
		this.javaSparkContext = sparkConfigService.getJavaSparkContext(getClass().getName(),true);
		ReduceAndGroupByKey reduceAndGroupByKey = new ReduceAndGroupByKey();
		reduceAndGroupByKey.perform(this.javaSparkContext);
		
	}
}
