package lab.spark;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SparkCommonConfig {

	final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:SS MM-dd-YY");
	
	@Value("${spark.master.host}")
	protected String spark_master_host;
	
	@Value("${spark.master.port}")
	protected String spark_master_port;

	protected String spark_Driver_Host = null;
	
	@PostConstruct
	public void init() throws UnknownHostException {
		spark_Driver_Host = InetAddress.getLocalHost().getHostAddress();
	}
}
