package net.idans.covid19.tweets;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.idans.json.JSONUtilities;
import net.idans.kafka.KafkaUtilities;
import net.idans.log.LogUtilities;
import net.idans.twitter.TwitterUtilities;

public abstract class KafkaToStorageAbstractRunnable implements Runnable{
	
	  private Logger logger;// = LoggerFactory.getLogger(KafkaToStorageAbstractRunnable.class.getName());

      private CountDownLatch latch;
      private KafkaConsumer<String, String> consumer;
      private IKafkaTweetExporter exporter;
      
      /**
       * Constructor:
       * 1. Apply the latch
       * 2. create a Kafka Consumer
       * 3. Subscribe to the topic
       * @param bootstrapServer
       * @param groupId
       * @param topic
       * @param latch
       */
      public KafkaToStorageAbstractRunnable(String bootstrapServer, String groupId, String topic, CountDownLatch latch, IKafkaTweetExporter kafkaToExternalExporter, Logger logger) {
    	 this.latch = latch;
 		 this.consumer = KafkaUtilities.createKafkaConsumer(bootstrapServer, groupId);
 		 this.consumer.subscribe(Arrays.asList(topic));
 		 this.exporter = kafkaToExternalExporter;
 		 this.logger = logger;
    	  
    	  
      }
      
      
	@Override
	public void run() {
		
		 //poll for the data
        try {
            while (true) {
           	
                ConsumerRecords<String, String> records =
                        consumer.poll(Duration.ofMillis(100)); //new in Kafka 2.2.0

                for (ConsumerRecord<String, String> record : records) {
                	
                	//Log the records
                    logger.info("Partition: " + record.partition() + ", Offset: " + record.offset());
                    logger.info("Key: " + record.key() + ", Value: " + record.value());
                    
                    

                	String tweetText = TwitterUtilities.getTextFromTweet(record.value());
                	
                	String normalizedTweetText = JSONUtilities.normalizeJsonString(tweetText);
                	
                	String idString = TwitterUtilities.getIdStrFromTweet(record.value());
                	
                	String lang = TwitterUtilities.getLangFromTweet(record.value());
                	
                	String normalizedJsonTweetText = JSONUtilities.trim(record.value());
                	
                	String timestamp = TwitterUtilities.getTimestampMsTweet(record.value());
                	
                	//Although the original Kafka Stream filters "EN" tweets, make sure you also put
                	//"EN" tweets here (maybe there are tweets from older versions that weren't filtered
                	//by the Kakfa input stream)
                	
                	if (lang.equalsIgnoreCase("en")==false)
                	{
                		logger.info("Skipping record because tweet is not in English. Tweet text: \""+normalizedTweetText+"\"");
                		continue;
                	}
                	
                    try
                    {
                    	exporter.exportTweet(
	                    									idString,
	                    									normalizedTweetText,//TwitterUtilities.getTextFromTweet(record.value()),
	                    									lang,
	                    									timestamp,
	                    									normalizedJsonTweetText);
                    }
                    catch(Exception e)
                    {
                    	logger.error(e.getMessage(), e);
                    	LogUtilities.logErrorMessage(logger, "Error for tweet with text \""+tweetText+"\""
                    			+ "\n Normalized tweet text is : \""+tweetText+"\""
                    			+ "\nError is: "+e.getMessage());
                    }
                    
                    
                    try {
                        Thread.sleep(500); //we do this only so we could see the prints slowly
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    
                    logger.info("Exported tweet with text: "+normalizedTweetText);

                }
            }
        }
        catch(WakeupException e){
            logger.info("Received shutdown signal");

        }
        finally{
            consumer.close();
             //tell our main code we're down with the consumer
             latch.countDown();
        }
        
        
		
		
	}
	
	  public void shutdown(){

           //the wakeup() method is a special method to interrupt consumer.poll()
           //it will throw the exception WakeUpException
           consumer.wakeup();

   }

}
