package net.idans.covid19.tweets;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaToCSVRunnable extends KafkaToStorageAbstractRunnable implements Runnable {
	
	private static Logger logger = LoggerFactory.getLogger(KafkaToCSVRunnable.class.getName());

	private static final IKafkaTweetExporter csvUtilities = new CSVUtilities();

	public KafkaToCSVRunnable(String bootstrapServer, String groupId, String topic, CountDownLatch latch) {
		super(bootstrapServer, groupId, topic, latch, csvUtilities, logger);

	}

}
