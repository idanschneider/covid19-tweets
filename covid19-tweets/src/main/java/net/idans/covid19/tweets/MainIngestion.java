package net.idans.covid19.tweets;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.idans.kafka.KafkaUtilities;

/**
 * Implementation 1: 1. Read tweets from Twitter about COVID19 2. Store them in
 * Kafka Topic 3. Read Tweets from Kafka topic in batches into Spark 4. Analyze
 * Tweets - popular words (Spark RDD) 5. Optional - Categorize tweets?
 * (K-Clustering: Spark ML)
 * 
 * 
 * * Implementation 2 (future): 1. Read tweets from Twitter about COVID19 2.
 * Store them in Kafka Topic (stream) 3. Spark reading from Kafka Topic and
 * ingesting to database (stream==microbatches) so that they'll be stored
 * forever 4. Component (Spark) that reads from database in batches into Spark
 * 6. Analyze Tweets - popular words (Spark RDD) 7. Optional - Categorize
 * tweets? (K-Clustering: Spark ML)
 * 
 * 
 * @author Idan
 *
 */
public class MainIngestion {

	public static Logger logger = LoggerFactory.getLogger(MainIngestion.class.getName());
	
	private static enum KAFKA_EXPORT_TYPES {KAFKA_TO_ES, KAFKA_TO_CSV};

	public static void main(String[] args) {

		startTwitterKafkaProducerThread();
		startKafkaToESThread();
		startKafkaToCSVThread();

	}

	private static void startKafkaToESThread() {
		startThreadWithCountdownLatch(KAFKA_EXPORT_TYPES.KAFKA_TO_ES);
		
	}

	private static void startKafkaToCSVThread() {
		startThreadWithCountdownLatch(KAFKA_EXPORT_TYPES.KAFKA_TO_CSV);
		
	}


	private static void startTwitterKafkaProducerThread() {
		// Going with Implementation 1
		TwitterProducerRunnable twitterProducerRunnable = new TwitterProducerRunnable();
		Thread twitterProducerThread = new Thread(twitterProducerRunnable);
		twitterProducerThread.start();
	}

	private static void startThreadWithCountdownLatch(KAFKA_EXPORT_TYPES exportType) {
		
		new Thread(()->{
			// Main purpose of latch is to shutdown the application correctly
			CountDownLatch latch = new CountDownLatch(1);
			Runnable kafkaExportRunnable;
			
			
			if (exportType == KAFKA_EXPORT_TYPES.KAFKA_TO_CSV)
			{
				kafkaExportRunnable = new KafkaToCSVRunnable(KafkaUtilities.DEFAULT_BOOTSTRAP_SERVER,
					Covid19Consts.KAKFA_TO_CSV_GROUP_ID, Covid19Consts.TOPIC_NAME_COVID19, latch);
			}
			else if (exportType == KAFKA_EXPORT_TYPES.KAFKA_TO_ES)
			{
				kafkaExportRunnable = new KafkaToESRunnable(KafkaUtilities.DEFAULT_BOOTSTRAP_SERVER,
					Covid19Consts.KAKFA_TO_ES_GROUP_ID, Covid19Consts.TOPIC_NAME_COVID19, latch);
			
				
			}
			else
			{
				//default
				kafkaExportRunnable = new KafkaToCSVRunnable(KafkaUtilities.DEFAULT_BOOTSTRAP_SERVER,
						Covid19Consts.KAKFA_TO_CSV_GROUP_ID, Covid19Consts.TOPIC_NAME_COVID19, latch);
				
			}
			
			Thread kafkaToESProducerThread = new Thread(kafkaExportRunnable);
			
			kafkaToESProducerThread.start();

			// add a shutdown hook
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				logger.info("Caught shutdown hook");
				((KafkaToStorageAbstractRunnable) kafkaExportRunnable).shutdown();
			}));

			try {
				latch.await(); // wait until the application is over
			} catch (InterruptedException e) {
				logger.error("Application got interrupoted", e);
			} finally {
				logger.info("Application is closing");
			}

			
		}
		).start();
	}
		
}
