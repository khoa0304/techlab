package lab.Solr;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.testng.annotations.Test;

public class TestSolr1 {

	
	@Test
	public void testSol1() throws SolrServerException, IOException {
	
		
		String urlString = "http://144.91.109.48:8983/solr/solrlab";
		Http2SolrClient solr = new Http2SolrClient.Builder(urlString).build();
		solr.setParser(new XMLResponseParser());// TODO Auto-generated constructor stub
		
		SolrInputDocument document = new SolrInputDocument();
		document.addField("id", 123456l);
		document.addField("name", "My Pixel 3XL");
		document.addField("price", 599.99);
		solr.add(document);
		solr.commit();
		
		solr.addBean( new ProductBean(888l, "Apple iPhone 6s", 299.99) );
		solr.commit();
		
		SolrQuery query = new SolrQuery();
		query.set("q", "price:599.99");
		QueryResponse response = solr.query(query);
		 
		SolrDocumentList docList = response.getResults();
		assertEquals(docList.getNumFound(), 1);
		 
		for (SolrDocument doc : docList) {
			
		     assertEquals ( Long.valueOf((String)doc.getFieldValue("id")), Long.valueOf(123456));
		     assertEquals((Double) doc.getFieldValue("price"), (Double) 599.99);
		}
		
		solr.close();
	}
	
	
}
