package lab.spark;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;

public class WindowsCommonConfig {

	final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:SS MM-dd-YY");
	
	public String winHome = System.getenv("WINDOWS_HOME");
	
	public static final String SPARK_MASTER_IP_ADDRESS = "144.91.109.48";
	
	public static final String SPARK_MASTER_PORT = "7077";
	
	public static String SPARK_DRIVER_HOST = "10.8.0.2";
	
	static {
		String pathSeprator = File.separator;
		if(pathSeprator.equals("\\")) {
			System.setProperty("hadoop.home.dir","D:/lab/spark");	
		}
		
		try {
			SPARK_DRIVER_HOST = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}
