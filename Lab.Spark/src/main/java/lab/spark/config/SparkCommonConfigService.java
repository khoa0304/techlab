package lab.spark.config;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;

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
		
		Enumeration<NetworkInterface> networkInterfaces;
		try {
			networkInterfaces = NetworkInterface.getNetworkInterfaces();
			while(networkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = networkInterfaces.nextElement();
				List<InterfaceAddress> interfaceAddressList = networkInterface.getInterfaceAddresses();
				for(InterfaceAddress interfaceAddress : interfaceAddressList) {
					InetAddress inetAddress = interfaceAddress.getAddress();
					if(! inetAddress.isLoopbackAddress()) {
						String hostAddress = inetAddress.getHostAddress();
						//TODO to be configured externally
						if(hostAddress.indexOf("12.8") !=-1 || hostAddress.indexOf("10.15") !=-1) {
							spark_Driver_Host = hostAddress;
						}
					}
				}
			}
		} catch (SocketException e) {
		    logger.warn("Error obtaining network interfact to figure out spark driver IP addresss ",e);
			spark_Driver_Host = InetAddress.getLocalHost().getHostAddress();
		}
		
		//spark_Driver_Host = .;
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
