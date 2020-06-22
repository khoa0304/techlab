package lab.spark.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkCommonConfig {

	@Value("${spark.master.host.port}")
	protected String spark_master_host_port;
	
	//@Value("${spark.master.port}")
	//protected String spark_master_port;

	protected String spark_Driver_Host = null;
	
	@PostConstruct
	public void init() throws UnknownHostException {
		spark_Driver_Host = InetAddress.getLocalHost().getHostAddress();
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
