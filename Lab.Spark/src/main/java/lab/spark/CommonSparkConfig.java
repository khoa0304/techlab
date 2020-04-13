package lab.spark;

import java.util.Date;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

public class CommonSparkConfig extends WindowsCommonConfig {

	protected SparkSession sparkSession;
	protected JavaSparkContext javaSparkContext;
	
	public CommonSparkConfig() {
		
		this.sparkSession = getSparkSession(getClass().getName());
		//JavaSparkContext sparkContext = new JavaSparkContext(getSparkConf(getClass().getName()));//.sparkContext());
		javaSparkContext = new JavaSparkContext(this.sparkSession.sparkContext());
	}
	
	protected SparkConf getSparkConf(String className) {
		
		SparkConf sparkConf = new SparkConf();
		sparkConf.setAppName(className+": " + simpleDateFormat.format(new Date()));
		sparkConf.setMaster("spark://"+SPARK_MASTER_IP_ADDRESS+":"+SPARK_MASTER_PORT)	    
		 .set("spark.driver.host",SPARK_DRIVER_HOST)
		 //.setJars(new String[]{"C:\\mvnrepos2\\lab\\spark\\lab.spark\\0.0.1-SNAPSHOT\\lab.spark-0.0.1-SNAPSHOT.jar"});
		 .setJars(new String[]{"/home/khoa/.m2/repository/lab/spark/lab.spark/0.0.1-SNAPSHOT/lab.spark-0.0.1-SNAPSHOT.jar"});
		  //.set("spark.eventLog.dir", "/media/sf_SharedVol/spark/logs")
		  //.set("spark.eventLog.enabled", "true")
		
		return sparkConf;
	}
	
	
	protected SparkSession getSparkSession(String className) {

		SparkSession sparkSession = SparkSession.builder()
				.master("spark://"+SPARK_MASTER_IP_ADDRESS+":"+SPARK_MASTER_PORT)
				.appName(className+": " + simpleDateFormat.format(new Date()))
				.config("spark.driver.host",SPARK_DRIVER_HOST)
				.config("spark.executor.memory", "3g")
				.config("spark.jars","C:\\mvnrepos2\\lab\\spark\\lab.spark\\0.0.1-SNAPSHOT\\lab.spark-0.0.1-SNAPSHOT.jar")
				//.config("spark.jars","/home/khoa/.m2/repository/lab/spark/lab.spark/0.0.1-SNAPSHOT/lab.spark-0.0.1-SNAPSHOT.jar")
				.getOrCreate();
		
		return sparkSession;
	}
}
