package net.idans.kafka;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

public class KafkaUtilities {
	
	public static final String DEFAULT_BOOTSTRAP_SERVER = "127.0.0.1:9092";

	public static KafkaProducer<String, String> createKafkaProducer(String bootstrapServers) {
		// create producer properties
		Properties properties = new Properties();

//		String bootstrapServers = "127.0.0.1:9092";
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		// properties to create a SAFE PRODUCER
		properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
		// once we set the idempotent producer, we don't really need to set the
		// following ones, but it's better
		// to explicitly set them (rather than hope that they are indeed the default)
		properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
		properties.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));
		properties.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5"); // Kafka 2.0 >= 1,.1, so we
		// can keep this as 5, use 1 otherwise.

		// high throughput producer (at the expense of a bit of latency and CPU usage)
		properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
		properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "20");
		properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32 * 1024)); // 32KB batch size

		// create the producer
		// We chose that key is String, value is String. So we need
		// KafkaProducer<String,String>
		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
		return producer;

	}
	
	public static KafkaConsumer<String, String> createKafkaConsumer(String bootstrapServers, String groupId) {
		// create producer properties
		Properties properties = new Properties();

		  
		  properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,  bootstrapServers);
          properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
          properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
          properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
          properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		// create the producer
		// We chose that key is String, value is String. So we need
		// KafkaProducer<String,String>
		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
		return consumer;

	}
	
	
	
	public static boolean topicExists(String topicName) throws ExecutionException, InterruptedException
	{
		Properties prop = new Properties();
	    prop.setProperty("bootstrap.servers", "localhost:9092");
	    AdminClient admin = AdminClient.create(prop);
	    boolean doesTopicExist = admin.listTopics().names().get().stream().anyMatch(theTopicName -> theTopicName.equalsIgnoreCase(topicName));
	    return doesTopicExist;
	}

}
