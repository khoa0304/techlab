package lab.spark;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SparkConfigService {
	
	public final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:SS MM-dd-YY");
	
	@Autowired
	private SparkCommonConfig sparkCommonConfig;
	
	public JavaSparkContext getJavaSparkContext() throws UnknownHostException {
		SparkSession sparkSession = getSparkSession(getClass().getName());
		JavaSparkContext javaSparkContext = new JavaSparkContext(sparkSession.sparkContext());
		//JavaSparkContext sparkContext = new JavaSparkContext(getSparkConf(getClass().getName()));//.sparkContext());
		return javaSparkContext;
	}
	
	public SparkConf getSparkConf(String className) throws UnknownHostException {
		
		SparkConf sparkConf = new SparkConf();
		sparkConf.setAppName(className+": " + simpleDateFormat.format(new Date()));
		sparkConf.setMaster("spark://"+sparkCommonConfig.getSpark_master_host()+":"+sparkCommonConfig.getSpark_master_port())    
				 .set("spark.driver.host",sparkCommonConfig.getSpark_Driver_Host())
		         .setJars(new String[]{"target/lab-service-spark-0.0.1-SNAPSHOT.jar"});
		return sparkConf;
	}
	
	
	public SparkSession getSparkSession(String className) {

		SparkSession sparkSession = SparkSession.builder()
				.master("spark://"+sparkCommonConfig.getSpark_master_host()+":"+sparkCommonConfig.getSpark_master_port()) 
				.appName(className+": " + simpleDateFormat.format(new Date()))
				.config("spark.driver.host",sparkCommonConfig.getSpark_Driver_Host())
				.config("spark.executor.memory", "4g")
				.config("spark.jars","target/lab-service-spark-0.0.1-SNAPSHOT.jar")
				.getOrCreate();
		
		return sparkSession;
	}
}
