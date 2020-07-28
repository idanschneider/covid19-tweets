package net.idans.covid19.tweets.spark;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.idans.covid19.tweets.Covid19Consts;
import net.idans.covid19.tweets.KafkaToCSVRunnable;
import scala.Tuple2;

public class COVID19SparkRDDWordCount implements Runnable {
	
	private static Logger logger = LoggerFactory.getLogger(COVID19SparkRDDWordCount.class.getName());

	@Override
	public void run() {
		
		org.apache.log4j.Logger.getLogger("org.apache").setLevel(Level.WARN);
		SparkSession spark = SparkSession.builder().appName("COVID19-Tweets-word-count").master("local[*]")
												.config("spark.sql.warehouse.dir", "file:///d:/dev/java-spark-course/tmp")
												.getOrCreate();
				
		//Read can read from CSV, JDBC, JSON, text files, etc.
		//head=true --> the CSV contains a head line
		Dataset<Row> tweetsDataset = spark.read().option("header", true).csv("src/main/resources/covid19-tweets.csv");
		
		tweetsDataset.show(20,false); //show(print) the first 20 rows of the dataset
		
		
		Dataset<Row> tweetTextDataset = tweetsDataset.select(Covid19Consts.COVID19_CSV_TEXT_COLUMN_NAME);
		tweetTextDataset.show(20, false);
		
				
		//Now build an RDD only from the second column of the CSV
		JavaRDD<String> tweetTextRdd = tweetTextDataset.javaRDD().map(row -> row.mkString());
		tweetTextRdd.take(10).forEach(System.out::println);
		
		//the regex says: anything that's not a letter from a to z or from A to Z or space.
		//In this case, anything that's like the above (not a letter etc) we're replacing with a blank
		//Thus effectively we're REMOVING anything that's not a letter or not a space. So we're leaving only words and spaces
		//We also turn ever word to lowercase
		JavaRDD<String> lettersOnlyRdd = tweetTextRdd.map(sentence -> sentence.replaceAll("[^a-zA-Z\\s]", "").toLowerCase());
		
		JavaRDD<String> removeBlankLines = lettersOnlyRdd.filter(sentence -> sentence.trim().length() > 0);
		
		JavaRDD<String> justWords = removeBlankLines.flatMap( sentence -> Arrays.asList(sentence.split(" ")).iterator());
		
		JavaRDD<String> blankWordsRemoved = justWords.filter(word -> word.trim().length() > 0);
		
		JavaRDD<String> justInterestingWords = blankWordsRemoved.filter( word -> SparkUtilities.isNotBoring(word));
		
		JavaPairRDD<String, Long> pairRdd = justInterestingWords.mapToPair(word -> new Tuple2<String,Long>(word, 1L));
		
		JavaPairRDD<String, Long> totals = pairRdd.reduceByKey((value1, value2)-> value1+value2);
		
		JavaPairRDD<Long,String> switched = totals.mapToPair(tuple -> new Tuple2<Long, String>(tuple._2, tuple._1));
		
		JavaPairRDD<Long,String> sorted = switched.sortByKey(false);
		
		System.out.println("Printing the most frequent words");
		System.out.println("================================");
		
		List<Tuple2<Long,String>> results = sorted.take(100);
		
		results.forEach(System.out::println);
//		
//		sc.close();
//		
		
		
		
//		
//		
//		
//		
//		//Suppress warning
//		//System.setProperty("hadoop.home.dir",  "d:/hadoop");
//		System.setProperty("hadoop.home.dir",  "c:/winutils");
//		
//		Logger.getLogger("org.apache").setLevel(Level.WARN);
//		
//		SparkConf conf = new SparkConf().setAppName("COVID19-Tweets-word-count").setMaster("local[*]");
//		JavaSparkContext sc = new JavaSparkContext(conf);
//		
//		//step 1. Load input.txt to an RDD
//		JavaRDD<String> tweetsRdd = sc.textFile("src/main/resources/subtitles/covid19-tweets.csv");
//		
//		//Now build an RDD only from the second column of the CSV
//		JavaRDD<String> tweetTextRdd = tweetsRdd.map((row)-> row.split(","))
//		
//		//the regex says: anything that's not a letter from a to z or from A to Z or space.
//		//In this case, anything that's like the above (not a letter etc) we're replacing with a blank
//		//Thus effectively we're REMOVING anything that's not a letter or not a space. So we're leaving only words and spaces
//		//We also turn ever word to lowercase
//		JavaRDD<String> lettersOnlyRdd = initialRdd.map(sentence -> sentence.replaceAll("[^a-zA-Z\\s]", "").toLowerCase());
//		
//		JavaRDD<String> removeBlankLines = lettersOnlyRdd.filter(sentence -> sentence.trim().length() > 0);
//		
//		JavaRDD<String> justWords = removeBlankLines.flatMap( sentence -> Arrays.asList(sentence.split(" ")).iterator());
//		
//		JavaRDD<String> blankWordsRemoved = justWords.filter(word -> word.trim().length() > 0);
//		
//		JavaRDD<String> justInterestingWords = blankWordsRemoved.filter( word -> Util.isNotBoring(word));
//		
//		JavaPairRDD<String, Long> pairRdd = justInterestingWords.mapToPair(word -> new Tuple2<String,Long>(word, 1L));
//		
//		JavaPairRDD<String, Long> totals = pairRdd.reduceByKey((value1, value2)-> value1+value2);
//		
//		JavaPairRDD<Long,String> switched = totals.mapToPair(tuple -> new Tuple2<Long, String>(tuple._2, tuple._1));
//		
//		JavaPairRDD<Long,String> sorted = switched.sortByKey(false);
//		
//		List<Tuple2<Long,String>> results = sorted.take(10);
//		
//		results.forEach(System.out::println);
//		
//		sc.close();
		
		
		spark.close();
		
	}
	
	
	

}
