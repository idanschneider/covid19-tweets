package net.idans.covid19.tweets.spark;

public class MainAnalyzeData {

	public static void main(String[] args) {
		
		//SparkUtilities.isBoring("fofo");
		new COVID19SparkRDDWordCount().run();
	

	}

}
