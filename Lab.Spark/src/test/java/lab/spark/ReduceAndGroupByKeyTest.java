package lab.spark;

import java.net.UnknownHostException;

import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.Test;

import lab.spark.sample.ReduceAndGroupByKey;

@SpringBootTest
public class ReduceAndGroupByKeyTest extends CommonTestSetup {

	@Test
	public void test1() throws UnknownHostException  {
		 
		ReduceAndGroupByKey reduceAndGroupByKey = new ReduceAndGroupByKey();
		reduceAndGroupByKey.perform(sparkConfigService.getJavaSparkContext());
		
	}
}
