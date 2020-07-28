package net.idans.covid19.tweets;

import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.idans.kafka.KafkaUtilities;
import net.idans.kafka.RunnableWithParams;

public class ElasticSearchDBConnector {
	
	
	private static Logger logger = LoggerFactory.getLogger(ElasticSearchDBConnector.class.getName());
	
	public static void runConsumerInThread()
	{
		
		
		
		  //create a Kafka consumer in a new thread
        CountDownLatch latch = new CountDownLatch(1); //maximum of 1 worker
        
        String bootstrapServer = "127.0.0.1:9092";
        String groupId = Covid19Consts.ELASTIC_SEARCH_INGESTION_APP_GROUP_ID;
        String topic = Covid19Consts.TOPIC_NAME_COVID19;
        
 
        
        
        Runnable kafkaConsumerRunnable = new KafkaToESRunnable(bootstrapServer, groupId, topic, latch);
        
        //start the thread
        Thread kafkaConsumerThread = new Thread(kafkaConsumerRunnable);
        kafkaConsumerThread.start();
        
        //add a shutdown hook
        
        Runtime.getRuntime().addShutdownHook(new Thread(  () -> {
            logger.info("Caught shutdown hook");
            ((KafkaToESRunnable)kafkaConsumerRunnable).shutdown();

            try {
                latch.await();
            } catch (InterruptedException e) {
            	logger.error("Application got interrupoted", e);
                e.printStackTrace();
            }

            logger.info("Application has exited");

        }
        ));

         try {
             latch.await(); //wait until the application is over
         }
         catch(InterruptedException e) {
             logger.error("Application got interrupted", e);
         }
         finally {
             logger.info("Application is closing");
         }
        
	}

}
