package lab.spark.sample;

import java.net.UnknownHostException;

import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.Test;

@SpringBootTest
public class MapAndFilterTest extends CommonTestSetup {
	
	@Test
	public void test1() throws UnknownHostException, ClassNotFoundException {
	
		this.javaSparkContext = sparkConfigService.getJavaSparkContext(getClass().getName(),true);
		MapAndFilter mapAndFilter = new MapAndFilter(); 
		mapAndFilter.perform(javaSparkContext);		
	}

	
}
