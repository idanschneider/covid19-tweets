package net.idans.covid19.tweets;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSVUtilities implements IKafkaTweetExporter{
	
private static Logger logger = LoggerFactory.getLogger(CSVUtilities.class.getName());
	
//	public static final String COVID19_TWEETS_INDEX_NAME = "covid19-tweets";
	
	public static final String COVID19_CSV_FILE_NAME = "src/main/resources/covid19-tweets.csv";
	public static final String[] COVID19_CSV_FILE_HEADERS = new String[] {
							Covid19Consts.COVID19_CSV_ID_COLUMN_NAME,//"id",
							Covid19Consts.COVID19_CSV_TEXT_COLUMN_NAME,//"text",
							Covid19Consts.COVID19_CSV_LANG_COLUMN_NAME,//"lang",
							Covid19Consts.COVID19_CSV_TIMESTAMP_MS_COLUMN_NAME,//"timestamp_ms",
							Covid19Consts.COVID19_CSV_TWEET_JSON_COLUMN_NAME,//"tweet_json"
							};

	@Override
	public void exportTweet(String tweetId, String tweetText, String tweetLanguage, String tweetTimestamp, String tweetJson)
			throws IOException {
		
		String[] csvRow = tweetDetailsToCSVRow(tweetId, tweetText, tweetLanguage, tweetTimestamp, tweetJson);
		writeToCSVFile(COVID19_CSV_FILE_NAME, COVID19_CSV_FILE_HEADERS, csvRow);
		logger.info("The following row was written:"+Arrays.toString(csvRow));
		
		
		
		
	}
	
	String[] tweetDetailsToCSVRow(String tweetId, String tweetText, String tweetLanguage, String tweetTimestamp, String tweetJson)
	{
		return new String[] {tweetId, tweetText, tweetLanguage, tweetTimestamp, tweetJson};
	}
	
	public void writeToCSVFile(String fileName,  String[] headers, String[] csvRow) throws IOException {
		
		//check if file exists
		File file = new File(fileName);
		CSVPrinter printer;
		if (file.exists()==false)
		{
			FileWriter out = new FileWriter(fileName);
		    printer = new CSVPrinter(out, CSVFormat.DEFAULT
		      .withHeader(headers));
		    
		}
		else
		{
			//idempotence - check if record already exists. If so, skip it.
			//Unique ID of record is unique ID of tweet
			//TODO: Check if the record appears already in Elastic. If so, assume it appears in the CSV
			//ALTERNATIVELY - read the entire CSV when thread begins, sort by ID, and then check if it exists.
			//This will be memory consuming so not recommended.
			
			
			//append
			FileWriter out = new FileWriter(fileName, true); //<==true means append
			printer = new CSVPrinter(out, CSVFormat.DEFAULT);
				      
		}
		printer.printRecord(csvRow[0], csvRow[1], csvRow[2], csvRow[3], csvRow[4]);
	    printer.close();
		
		
	    
	}
	
	public static void readFromCSVFile()
	{
		
	}

}
