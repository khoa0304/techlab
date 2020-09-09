package lab.spark.config;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lab.spark.dto.FileUploadContentDTO;
import lab.spark.dto.SentencesDTO;
import lab.spark.dto.WordsPerSentenceDTO;

@Service
public class SparkConfigService {

	public final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:SS MM-dd-YY");

	@Autowired
	private SparkCommonConfigService sparkCommonConfig;

	@Autowired
	private CassandraConfigService cassandraConfig;

	private String jarLocation = null;
	
	@PostConstruct
	public void init() {
		//File file = new File(".");
		//System.out.println("=====> Current Loc " + file.getAbsolutePath());
		jarLocation="target/lab-service-spark-0.0.1-SNAPSHOT.jar";
	}
	


	public SparkConf getSparkConfig(String className) throws UnknownHostException, ClassNotFoundException {

		SparkConf sparkConf = new SparkConf();
		sparkConf.setAppName(className + ": " + simpleDateFormat.format(new Date()));
		sparkConf
				.setMaster("spark://" + sparkCommonConfig.getSparkMasterHostPort())
				.set("spark.driver.host", sparkCommonConfig.getSpark_Driver_Host())
				.set("spark.local.ip",sparkCommonConfig.getSpark_Driver_Host())
				.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
				.set("spark.kryo.registrationRequired", "true")
				.registerKryoClasses(
					      new Class<?>[] {
					    		   FileUploadContentDTO.class,
					    		   SentencesDTO.class,
					    		   SentencesDTO[].class,
					    		   WordsPerSentenceDTO.class,
					    		   WordsPerSentenceDTO[].class
					    		   }
					      
					    )
				.setJars(new String[] { jarLocation });
		return sparkConf;
	}

	//~~~~~~~~~~~~~ Below Not being used for actual code yet ~~~~~~~~~~~~ //
	
	public JavaSparkContext getJavaSparkContext(String className) throws UnknownHostException {
		SparkSession sparkSession = getSparkSession(className);
		JavaSparkContext javaSparkContext = new JavaSparkContext(sparkSession.sparkContext());
		// JavaSparkContext sparkContext = new
		// JavaSparkContext(getSparkConf(getClass().getName()));//.sparkContext());
		return javaSparkContext;
	}
	
	
	public SparkSession getSparkSession(String className) {

		SparkSession sparkSession = getBasicSparkSessionBuilder(className).getOrCreate();
		return sparkSession;
	}

	// ~~~~~~~~~~~ For Cassandra ~~~~~~~~~~~~~//
	public SparkConf getSparkConfigForCassandra(String className) throws UnknownHostException {

		SparkConf sparkConf = new SparkConf();
		sparkConf.setAppName(className + ": " + simpleDateFormat.format(new Date()));
		sparkConf
				.setMaster("spark://" + sparkCommonConfig.getSparkMasterHostPort())
				.set("spark.driver.host", sparkCommonConfig.getSpark_Driver_Host())
				.set("spark.cassandra.connection.host", cassandraConfig.getContactPoints())
				.set("spark.cassandra.connection.port", String.valueOf(cassandraConfig.getPort()))
				.set("spark.cassandra.auth.username", cassandraConfig.getUsername())
				.set("spark.cassandra.auth.password", cassandraConfig.getPassword())
				.setJars(new String[] { jarLocation });

		return sparkConf;
	}

	public SparkSession getSparkSessionForCassandra(String className) {

		SparkSession sparkSession = getBasicSparkSessionBuilder(className)
				.config("spark.cassandra.connection.host", cassandraConfig.getContactPoints())
				.config("spark.cassandra.connection.port", String.valueOf(cassandraConfig.getPort()))
				.config("spark.cassandra.auth.username", cassandraConfig.getUsername())
				.config("spark.cassandra.auth.password", cassandraConfig.getPassword()).getOrCreate();

		return sparkSession;
	}

	
	private org.apache.spark.sql.SparkSession.Builder getBasicSparkSessionBuilder(String className){
		
		org.apache.spark.sql.SparkSession.Builder sparkSessionBuilder = SparkSession.builder()
				.master("spark://"+ sparkCommonConfig.getSparkMasterHostPort())
				.appName(className + ": " + simpleDateFormat.format(new Date()))
				.config("spark.jars", jarLocation)
				.config("spark.driver.host", sparkCommonConfig.getSpark_Driver_Host())
				// config("spark.driver.cores", "2")
				//.config("spark.shuffle.service.enabled", "false")
				//.config("spark.dynamicAllocation.enabled", "false")
				.config("SPARK_LOCAL_IP",sparkCommonConfig.getSpark_Driver_Host())
				.config("spark.driver.memory", "4g");
		
		
		
		return sparkSessionBuilder;
		
	}
}
