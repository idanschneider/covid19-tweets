package net.idans.covid19.tweets;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.idans.json.JSONUtilities;
import net.idans.kafka.KafkaUtilities;
import net.idans.log.LogUtilities;
import net.idans.twitter.TwitterUtilities;

/**
 * Implementation of KafkaConsumer in seperate Thread
 * @author Idan
 *
 */
public class KafkaToESRunnable extends KafkaToStorageAbstractRunnable implements Runnable {
	
	private static Logger logger = LoggerFactory.getLogger(KafkaToESRunnable.class.getName());
	
	  private static final IKafkaTweetExporter elasticSearchUtilites = new ElasticSearchUtilities();
	  
	  public KafkaToESRunnable(String bootstrapServer, String groupId, String topic, CountDownLatch latch)
	  {
		  super(bootstrapServer, groupId, topic, latch, elasticSearchUtilites, logger);
		    
      }
	

}
