package lab.spark.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkCommonConfigService {

	private Logger logger = LoggerFactory.getLogger(SparkCommonConfigService.class);
	
	@Value("${spark.master.host.port}")
	protected String spark_master_host_port;
	
	//@Value("${spark.master.port}")
	//protected String spark_master_port;

	protected String spark_Driver_Host = null;
	
	@PostConstruct
	public void init() throws UnknownHostException {
		spark_Driver_Host = InetAddress.getLocalHost().getHostAddress();
		logger.info("Spark Master {} - Spark Driver IP Address {}", this.spark_master_host_port, this.spark_Driver_Host);
	}

	public String getSparkMasterHostPort() {
		return this.spark_master_host_port;
	}

//	public String getSpark_master_port() {
//		return spark_master_port;
//	}

	public String getSpark_Driver_Host() {
		return spark_Driver_Host;
	}
}
