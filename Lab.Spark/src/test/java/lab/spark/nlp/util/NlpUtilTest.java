package lab.spark.nlp.util;

import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
	
	@Test
	public void testSumInteger() {
		
		Map<Integer,Integer> map =  new HashMap<>();
		
		map.put(10, 10);
		map.put(20, 20);
		map.put(30, 30);
		map.put(40, 40);
				
		int total = map.values().stream().map(new Function<Integer, Integer>() {

			@Override
			public Integer apply(Integer t) {
				return t;
			}
			
		}).collect(Collectors.summingInt(Integer:: intValue));
	
		assertEquals(total, 100);
	}
	
}
