package lab.spark.sample;

import java.net.UnknownHostException;

import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.Test;

import lab.spark.sample.MapAndFilter;

@SpringBootTest
public class MapAndFilterTest extends CommonTestSetup {
	
	@Test
	public void test1() throws UnknownHostException {
	
		this.javaSparkContext = sparkConfigService.getJavaSparkContext(getClass().getName());
		MapAndFilter mapAndFilter = new MapAndFilter(); 
		mapAndFilter.perform(javaSparkContext);		
	}

	
}
