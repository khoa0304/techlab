package lab.spark;

import java.net.UnknownHostException;
import java.util.Arrays;

import org.apache.spark.api.java.JavaPairRDD;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.Test;

import scala.Tuple2;

@SpringBootTest
public class JoinRDDTest extends CommonTestSetup {

	@Test
	public void test1() throws UnknownHostException {
		 
 
		JavaPairRDD<String,String> visitsRDD = JavaPairRDD.fromJavaRDD(javaSparkContext.parallelize(
											Arrays.asList(
												new Tuple2<String,String>("index.html", "1.2.3.4"),
												new Tuple2<String,String>("about.html", "3.4.5.6"),
												new Tuple2<String,String>("index.html", "1.3.3.1")
												)
											)
										);
 
		System.out.println(visitsRDD.collect().toString());
			
		JavaPairRDD<String,String> pageNamesRDD = JavaPairRDD.fromJavaRDD(javaSparkContext.parallelize(
											Arrays.asList(
												new Tuple2<String,String>("index.html", "Home"),
												new Tuple2<String,String>("about.html", "About")
												)
											)
										);
 
		System.out.println(pageNamesRDD.collect().toString());
 
		JavaPairRDD<String,Tuple2<String,String>> joinRDD = visitsRDD.join(pageNamesRDD);
		System.out.println(joinRDD.collect().toString());
	}
}
