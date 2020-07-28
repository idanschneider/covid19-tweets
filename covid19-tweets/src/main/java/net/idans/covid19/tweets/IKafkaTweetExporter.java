package net.idans.covid19.tweets;

import java.io.IOException;

public interface IKafkaTweetExporter {
	
	public void exportTweet(String tweetId, String tweetText, String tweetLanguage, String tweetTimestamp, String tweetJson) throws IOException;
	
	

}
