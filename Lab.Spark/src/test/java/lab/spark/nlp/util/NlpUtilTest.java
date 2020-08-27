package lab.spark.nlp.util;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import org.testng.annotations.Test;

public class NlpUtilTest {

	@Test
	public void testLoadEnglishStopWords() throws IOException {
		NlpUtil nlpUtil = NlpUtil.getInstance();
		Set<String> stopWords = nlpUtil.getStopWordsSet();
		assertTrue(stopWords.size() > 0);
	}
	
	@Test
	public void testNetworkAddress() throws SocketException {
		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		while(networkInterfaces.hasMoreElements()) {
			NetworkInterface networkInterface = networkInterfaces.nextElement();
			List<InterfaceAddress> interfaceAddressList = networkInterface.getInterfaceAddresses();
			for(InterfaceAddress interfaceAddress : interfaceAddressList) {
				InetAddress inetAddress = interfaceAddress.getAddress();
				System.out.println(inetAddress.getHostAddress());
			}
		}
	}
}
