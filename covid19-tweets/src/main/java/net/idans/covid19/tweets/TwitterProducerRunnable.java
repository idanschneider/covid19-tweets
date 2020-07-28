package net.idans.covid19.tweets;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spark_project.guava.collect.Lists;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

import net.idans.kafka.KafkaUtilities;
import net.idans.log.LogUtilities;
import net.idans.twitter.TwitterUtilities;

public class TwitterProducerRunnable implements Runnable
{
	Logger logger = LoggerFactory.getLogger(TwitterProducerRunnable.class.getName());

	final static List<String> COVID_19_TERMS = Lists.newArrayList("covid19", "coronavirus", "corona", "covid-19");
	final static List<String> TWEET_LANGUAGES = Lists.newArrayList("en");
	

    public TwitterProducerRunnable() {
    	
    }

   

    public void run() {

        BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(1000);

        //create a twitter client
        Client client = TwitterUtilities.createTwitterStreamingClient(msgQueue, COVID_19_TERMS, TWEET_LANGUAGES);


        // Attempts to establish a connection.
        client.connect();

        //create a Kafka producer
        KafkaProducer<String, String> producer = KafkaUtilities.createKafkaProducer(KafkaUtilities.DEFAULT_BOOTSTRAP_SERVER);

        Runtime.getRuntime().addShutdownHook(new Thread( ()-> {
            logger.info("stopping application...");
            logger.info("shutting down client from twitter...");
            client.stop();
            logger.info("closing producer...");
            producer.close();
            logger.info("done!");
        }));
        
        
        
        // on a different thread, or multiple different threads....
        validateTopicExists(client);
        long receivedTweetCount = 0;
        
        while (!client.isDone()) {
            String msg = null;
            try {
                msg = msgQueue.poll(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                client.stop();
            }

            if (msg!=null)
            {
            	receivedTweetCount++;
                logger.info("Tweet #"+receivedTweetCount+":"+ msg); //log the message to the info
                producer.send(new ProducerRecord<>(Covid19Consts.TOPIC_NAME_COVID19, null, msg), new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                        if (e != null){
                            //if there is an error:

                            logger.error("Something bad happened", e);
                        }
                    }
                });
            }
            
            //Idan - since Twitter limits me to 500,000 messages per month, then to slow things down,
            //every 100 tweets I'll let my code rest for half a minute
            if (receivedTweetCount%100==0) {
            	try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					LogUtilities.logErrorMessage(logger,e.getMessage());
				}
            	
            }
        }

        logger.info("End of application");



        //loop to send tweets to Kafka

    }



	private void validateTopicExists(Client client) {
        //check if topic exists
        boolean doesTopicExist = false;
        try {
			doesTopicExist = KafkaUtilities.topicExists(Covid19Consts.TOPIC_NAME_COVID19);
		} catch (ExecutionException e1) {
			e1.printStackTrace();
			LogUtilities.logErrorMessage(logger, e1.getMessage());
			client.stop();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			LogUtilities.logErrorMessage(logger, e1.getMessage());
			client.stop();
		}
        
		
		if (doesTopicExist==false)
        {
        	LogUtilities.logErrorMessage(logger, "The topic "+Covid19Consts.TOPIC_NAME_COVID19+" does not exist");
        	System.exit(1);
        }
		
	}



   
   

}
