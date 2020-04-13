package lab.connector;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.PlainTextAuthProvider;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

/*
 * Simple Java client test to connect to trial cluster, create a time series data table, fill it, query it, and save it as csv for graphing.
 */

public class CassTest1 {
	
	static String n1PubIP = "144.91.109.48";
	// static String n1PubIP = "192.168.16.2";
//	static String n2PubIP = "01.234.56.78";
//	static String n3PubIP = "01.23.456.78";
	//static String dcName = "hal_sydney"; // this is the DC name you used when created
	static String user = "cassandra";
	static String password = "welcome123";

	public static void main(String[] args) {

		Cluster.Builder clusterBuilder = Cluster.builder().addContactPoints(n1PubIP// , n2PubIP, n3PubIP // provide all
																					// 3 public IPs
		)// .withLoadBalancingPolicy(DCAwareRoundRobinPolicy.builder().withLocalDc(dcName).build())
		// // your local data center
		.withPort(9042).withAuthProvider(new PlainTextAuthProvider(user, password));

		Cluster cluster = null;
		try {

			cluster = clusterBuilder.build();

			Metadata metadata = cluster.getMetadata();
			System.out.printf("Connected to cluster: %s\n", metadata.getClusterName());

			for (Host host : metadata.getAllHosts()) {
				System.out.printf("Datacenter: %s; Host: %s; Rack: %s\n", host.getDatacenter(), host.getAddress(),
						host.getRack());
			}

			Session session = cluster.connect();

			ResultSet rs = session.execute(
					//"CREATE KEYSPACE IF NOT EXISTS test WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 2}");
					//"CREATE KEYSPACE IF NOT EXISTS test WITH replication = {'class': 'NetworkTopologyStrategy', 'cassandra_dc2': 2, 'cassandra_dc1':2 }");
					"CREATE KEYSPACE IF NOT EXISTS lab WITH replication = {'class': 'NetworkTopologyStrategy', 'cassandra_dc1':2 }");
			
			//rs = session.execute("DROP TABLE IF EXISTS test.emp");

			// @formatter:off
			String createTableQuery = "CREATE TABLE IF NOT EXISTS lab.textfile(" 
					+ "   file_category varchar,"
					+ "   file_name text," 
					+ "   file_length bigint," 
					+ "   file_metadata text,"
					+ "   file_content text ,"
					+ "   PRIMARY KEY ((file_category),file_length) )"
					+ "   WITH CLUSTERING ORDER BY (file_length ASC)"
					+ "   ";

			// @formatter:on
			session.execute(createTableQuery);
			
		
		
			rs = session.execute("select * from lab.textfile");

			for (Row rowN : rs) {
				System.out.println(rowN.toString());
			}

		} finally {
			if (cluster != null)
				cluster.close();
		}
	}
}
