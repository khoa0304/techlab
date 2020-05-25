package lab.spark;

import java.net.UnknownHostException;
import java.util.Date;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.stereotype.Service;

@Service
public class SparkConfigService extends SparkCommonConfig {
	
	
	public JavaSparkContext getJavaSparkContext() throws UnknownHostException {
		SparkSession sparkSession = getSparkSession(getClass().getName());
		JavaSparkContext javaSparkContext = new JavaSparkContext(sparkSession.sparkContext());
		//JavaSparkContext sparkContext = new JavaSparkContext(getSparkConf(getClass().getName()));//.sparkContext());
		return javaSparkContext;
	}
	
	public SparkConf getSparkConf(String className) throws UnknownHostException {
		
		SparkConf sparkConf = new SparkConf();
		sparkConf.setAppName(className+": " + simpleDateFormat.format(new Date()));
		sparkConf.setMaster("spark://"+spark_master_host+":"+spark_master_port)    
				 .set("spark.driver.host",spark_Driver_Host)
		         .setJars(new String[]{"target/lab.spark-0.0.1-SNAPSHOT.jar"});
		return sparkConf;
	}
	
	
	public SparkSession getSparkSession(String className) {

		SparkSession sparkSession = SparkSession.builder()
				.master("spark://"+spark_master_host+":"+ spark_master_port) 
				.appName(className+": " + simpleDateFormat.format(new Date()))
				.config("spark.driver.host",spark_Driver_Host)
				.config("spark.executor.memory", "4g")
				.config("spark.jars","target/lab-service-spark-0.0.1-SNAPSHOT.jar")
				.getOrCreate();
		
		return sparkSession;
	}
}
