package net.idans.covid19.tweets;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticSearchUtilities implements IKafkaTweetExporter {
	
	private static Logger logger = LoggerFactory.getLogger(ElasticSearchUtilities.class.getName());
	
	public static final String COVID19_TWEETS_INDEX_NAME = "covid19-tweets";
	
	
	public static RestHighLevelClient restHighLevelClient = null;
	
	
	private static final String INDEX_MAPPING_JSON = 
			 "\"mappings\": {\r\n" + 
			 "            \"properties\": {\r\n" + 
			 "                \"lang\": {\r\n" + 
			 "                    \"type\": \"text\",\r\n" + 
			 "                    \"fields\": {\r\n" + 
			 "                        \"keyword\": {\r\n" + 
			 "                            \"type\": \"keyword\",\r\n" + 
			 "                            \"ignore_above\": 256\r\n" + 
			 "                        }\r\n" + 
			 "                    }\r\n" + 
			 "                },\r\n" + 
			 "                \"text\": {\r\n" + 
			 "                    \"type\": \"text\",\r\n" + 
			 "                    \"fields\": {\r\n" + 
			 "                        \"keyword\": {\r\n" + 
			 "                            \"type\": \"keyword\",\r\n" + 
			 "                            \"ignore_above\": 256\r\n" + 
			 "                        }\r\n" + 
			 "                    }\r\n" + 
			 "                },\r\n" + 
			 "                \"timestamp_ms\": {\r\n" + 
			 "                    \"type\": \"long\",\r\n" + 
			 "                    \"fields\": {\r\n" + 
			 "                        \"keyword\": {\r\n" + 
			 "                            \"type\": \"keyword\",\r\n" + 
			 "                            \"ignore_above\": 256\r\n" + 
			 "                        }\r\n" + 
			 "                    }\r\n" + 
			 "                }\r\n" + 
			 "            }";


	
	
	/**
	 * Singleton
	 * @return
	 */
	public static RestHighLevelClient createHighLevelClientNoAuthentication() {
		
//		String hostname = "localhost";
		
		if (restHighLevelClient==null)
		{
			synchronized(ElasticSearchUtilities.class) {
				if (restHighLevelClient==null)
				{
					restHighLevelClient = new RestHighLevelClient(
					        RestClient.builder(
					                new HttpHost("localhost", 9200, "http"),
					                new HttpHost("localhost", 9201, "http")));
					
				}
			}
			
		}
	
				
		return restHighLevelClient;

	}
	
//
////Create a REST client to Elastic Search --> WITH AUTHENTICATION
//	public static RestHighLevelClient createHighLevelClientWithAuthentication() {
//
//		// From "Access" tab in Bonsai, I get the access URL:
//		// https://okjbfd08cb:5q8rvnoytw@idans-net-testing-6401390141.eu-central-1.bonsaisearch.net:443
//		// The above URL consists of the host, username and password, as follow:
//		// idans-net-testing-6401390141.eu-central-1.bonsaisearch.net
//		// okjbfd08cb
//		// 5q8rvnoytw
//
////      String hostname="idans-net-testing-6401390141.eu-central-1.bonsaisearch.net";
////      String username="okjbfd08cb";
////      String password="5q8rvnoytw";
//
//		String hostname = "localhost:9200";
//		String username = "okjbfd08cb";
//		String password = "5q8rvnoytw";
//
//		// don't do if you run a local ES
//		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
//
//		RestClientBuilder builder = RestClient.builder(new HttpHost(hostname, 443, "https"))
//				.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
//					@Override
//					public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
//						return httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
//					}
//				});
//		// The callback above says: Apply these credentials to any HTTP call
//
//		RestHighLevelClient client = new RestHighLevelClient(builder);
//		
//		
//		return client;
//
//	}
	
	
	/**
	 * Add a new record
	 * @param client
	 * @param tweetId
	 * @param tweetText
	 * @param tweetLanguage
	 * @param tweetTimestamp
	 * @throws IOException
	 */
	public void exportTweet(String tweetId, String tweetText, String tweetLanguage, String tweetTimestamp, String tweetJson) throws IOException
	{
		
		//Create a REST client for elastic search
        RestHighLevelClient esClient = ElasticSearchUtilities.createHighLevelClientNoAuthentication();
		
		//JSON to read all records from a table
		//http://localhost:9200/covid19-tweets/_search?pretty=true&q=*:*
		
		
		//For tweet with ID tweet-12345
		
		//GET:
		//http://localhost:9200/covid19-tweets/_doc/tweet-12345
		
		//IndexRequest indexRequest = new IndexRequest("covid19-tweets/_doc/tweet-"+tweetId);
		IndexRequest indexRequest = new IndexRequest(COVID19_TWEETS_INDEX_NAME);
		
		 indexRequest.id("tweet-"+tweetId);
//		 String jsonSourceString = "{" +
//			        "\"text\":\""+tweetText+"\"," +
//			        "\"lang\":\""+tweetLanguage+"\"," +
//			        "\"timestamp_ms\":\""+tweetTimestamp+"\"," +
//			        "\"tweetJson\": "+tweetJson+" "+
//			        "}";
		 
		 //For Elastic we will NOT store the entire original JSon, as doing it causes the
		 //notorious "Limit of total fields [1000] in index [] has been exceeded" error
		 String jsonSourceString = "{" +
			        "\"text\":\""+tweetText+"\"," +
			        "\"lang\":\""+tweetLanguage+"\"," +
			        "\"timestamp_ms\":\""+tweetTimestamp+"\""+
			        "}";
			 
		 indexRequest.source(jsonSourceString, XContentType.JSON);
		 
		 IndexResponse indexResponse = esClient.index(indexRequest, RequestOptions.DEFAULT);
		 

         String indexResponseId = indexResponse.getId();
         logger.info("Document with ID "+indexResponseId+", result is "+indexResponse.getResult());
         ;
		 
		
	}
	

	
	
	public static void closeClient(RestHighLevelClient client)
	{
		try {
			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
