package net.idans.covid19.tweets;

import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;

import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
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
public class MainTest {

	public static Logger logger = LoggerFactory.getLogger(MainTest.class.getName());

	public static void main(String[] args) {

		LocalDate localDate = LocalDate.parse("2020-07-21");
		long localDateLong = localDate.toEpochDay();

		SearchHit[] searchHits;
		searchHits = ElasticSearchUtilities.searchText(Covid19Consts.ES_COVID19_TWEETS_INDEX_NAME, "trump", 20);

		if (searchHits != null && searchHits.length!= 0)
		{
			for (SearchHit hit : searchHits) {
				System.out.println(hit);
			}
		}
		else
		{
			System.out.println("No results found");
		}

	}

}
