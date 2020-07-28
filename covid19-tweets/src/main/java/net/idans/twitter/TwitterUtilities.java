package net.idans.twitter;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spark_project.guava.collect.Lists;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

import net.idans.covid19.tweets.ElasticSearchUtilities;

public class TwitterUtilities {

	final static String consumerKey = "kNXhXUKoMcOmngmm1uMNIjJFz";
	final static String consumerSecret = "XHZ66XuUsvAMhR0yj5hMqFPbM1DS7cD2QRcMIohBtKfoXGOqVR";
	final static String token = "14647841-gxx9fiTqOvoBRXXvq4Lb46URCedxE739Tg1BrVrie";
	final static String secret = "L3MzYZQkqKHpjoMbwEyc0UoHlxQ8AVXEMyvtHIZp9kE72";
	
	private enum TweetTypeEnum {REGULAR, RETWEET, QUOTED};
	
	private static final Logger logger = LoggerFactory.getLogger(TwitterUtilities.class.getName());

	public static Client createTwitterStreamingClient(BlockingQueue<String> msgQueue, List<String> terms,
			List<String> languages) {
		/**
		 * Declare the host you want to connect to, the endpoint, and authentication
		 * (basic auth or oauth)
		 */
		Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
		StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
		// Optional: set up some followings and track terms
		// List<Long> followings = Lists.newArrayList(1234L, 566788L);
		hosebirdEndpoint.languages(languages); // filter only english tweets

		// hosebirdEndpoint.followings(followings);
		hosebirdEndpoint.trackTerms(terms);

		// // These secrets should be read from a config file
		Authentication hosebirdAuth = new OAuth1(consumerKey, consumerSecret, token, secret);

		ClientBuilder builder = new ClientBuilder().name("Hosebird-Client-01") // optional: mainly for the logs
				.hosts(hosebirdHosts).authentication(hosebirdAuth).endpoint(hosebirdEndpoint)
				.processor(new StringDelimitedProcessor(msgQueue));
		// .eventMessageQueue(eventQueue); // optional: use this if you want to process
		// client events

		Client hosebirdClient = builder.build();
		return hosebirdClient;

	}

	public static String getIdStrFromTweet(String tweetJson) {

		// gson library (see POM.XML for dependency)
		return JsonParser.parseString(tweetJson).getAsJsonObject().get("id_str").getAsString();

	}

	public static boolean isExtendedTweet(String tweetJson)
	{
		
		JsonElement elem = JsonParser.parseString(tweetJson).getAsJsonObject().get("extended_tweet");
		if (elem==null)
		{
			return false;
		}
		else
		{
			return true;
		}
		
		
	}
	
	public static boolean isRetweet(String tweetJson)
	{
		JsonElement elem = getRetweetedStatus(tweetJson);
		if (elem==null)
		{
			return false;
		}
		else
		{
			return true;
		}
		
//		String tweetText = JsonParser.parseString(tweetJson).getAsJsonObject().get("text").getAsString();
//		if (tweetText.startsWith("RT"))
//		{
//			return true;
//		}
//		return false;
	}
	
	public static JsonElement getRetweetedStatus(String tweetJson)
	{
		return JsonParser.parseString(tweetJson).getAsJsonObject().get("retweeted_status");
	}
	
	
	
	public static boolean isQuoted(String tweetJson)
	{
		JsonElement elem = JsonParser.parseString(tweetJson).getAsJsonObject().get("quoted_status");
		if (elem==null)
		{
			return false;
		}
		else
		{
			return true;
		}
		
	}
	
	


	private static boolean keyExists(JsonObject object, String searchedKey) {
		
		//System.out.println("Ni");
		boolean exists = object.has(searchedKey);
		if (exists == true)
		{
			//System.out.println("TRUE");
			return true;
		}
		//else
		
		Set<String> keys = object.keySet();
		Iterator<String> keysIterator = keys.iterator();
		while (keysIterator.hasNext()) {
			String key = (String) keysIterator.next();
			if (object.get(key) instanceof JsonObject) {
				exists = keyExists((JsonObject)object.get(key), searchedKey);
				if (exists == true)
				{
//					System.out.println("TRUE");
					return true;
				}
			}
		}
		
		return false;

	}
	
	
	private static String getTextFromNestedElement(String tweetJson, TweetTypeEnum tweetType)
	{
		String key = null;
		if (tweetType == TweetTypeEnum.RETWEET)
		{
			key = "retweeted_status";
		}
		else if (tweetType == TweetTypeEnum.QUOTED)
		{
			key = "quoted_status";
		}
		else
		{
			//regular - but should not happen
			logger.warn("Found regular tweet type for tweet with JSON"+tweetJson);
			return getTextFromTweetRegular(tweetJson, false); 
		}
		
		
		JsonElement nestedStatusElement = JsonParser.parseString(tweetJson).getAsJsonObject().get(key);
		if (nestedStatusElement!=null)
		{
			String nestedStatusJson = nestedStatusElement.getAsJsonObject().toString();
			boolean isNestedStatuesExtended = isExtendedTweet(nestedStatusJson);
			if (isNestedStatuesExtended) {
				
				JsonElement extendedTweetElement = nestedStatusElement
															.getAsJsonObject()
															.get("extended_tweet")
															.getAsJsonObject();
				
				String extendedTweetJson = extendedTweetElement.toString();
				return getTextFromTweetRegular(extendedTweetJson, true);
			}
			else
			{
				return getTextFromTweetRegular(nestedStatusJson, false);
			}
			
		}
		else
		{
			//should not happen
			logger.error("Could not find retweeted_status in JSON, will get text for this tweet instead. Json was:\n "+tweetJson);
			//just get the text from this tweet
			return getTextFromTweetRegular(tweetJson, false);
		}
		
	}
	
	public static String getTextFromTweet(String tweetJson) {

		boolean isRetweet = isRetweet(tweetJson);
		boolean isQuotedStatus = isQuoted(tweetJson);
		
		TweetTypeEnum tweetType;
		if (isRetweet)
		{
			 tweetType = TweetTypeEnum.RETWEET;
		}
		else if (isQuotedStatus)
		{
			 tweetType = TweetTypeEnum.QUOTED;
		}
		else
		{
			tweetType = TweetTypeEnum.REGULAR;
		}
		
		if (isRetweet || isQuotedStatus)
		{
			return getTextFromNestedElement(tweetJson, tweetType);
		}
		else
		{
			return getTextFromTweetRegular(tweetJson, false);
		}
		
	}


	public static String getTextFromTweetRegular(String tweetJson, boolean isTweetExtended) {
		// I needed to read the TEXT field from the tweetJson with a jsonReader (instead
		// of jsonParser)
		// because of errors I got claiming the text was malformed...

		JsonReader jsonReader = new JsonReader(new StringReader(tweetJson));
		jsonReader.setLenient(true); // <-- had to add this to avoid "Use JsonReader.setLenient(true) to accept
										// malformed JSON "
		boolean readNextValue = false;
//		boolean isTweetExtended = isExtendedTweet(tweetJson);


		try {
			while (jsonReader.hasNext()) {

				JsonToken nextToken = jsonReader.peek();
				if (nextToken == JsonToken.END_ARRAY || nextToken == JsonToken.END_OBJECT)
				{
					System.out.println("End of array or end of object "+nextToken);
					break;
				}

				if (JsonToken.BEGIN_OBJECT.equals(nextToken)) {

					jsonReader.beginObject();

				} 
				else if (JsonToken.END_OBJECT.equals(nextToken)) {
					jsonReader.endObject();
				}

				
				else if (JsonToken.NAME.equals(nextToken)) {

					String name = jsonReader.nextName();
					if (name.equalsIgnoreCase("text") && isTweetExtended == false) {
						readNextValue = true;
					}
					if (name.equalsIgnoreCase("full_text") && isTweetExtended == true) {
						readNextValue = true;
					}

				} else if (JsonToken.STRING.equals(nextToken)) {

					String value = jsonReader.nextString();

					if (readNextValue) {
		            	System.out.println("---------isTweetExtended="+isTweetExtended+" and returning the above value "+value);
						return value;
					}

				} else if (JsonToken.NUMBER.equals(nextToken)) {

					long value = jsonReader.nextLong();

				}
				else if (JsonToken.BOOLEAN.equals(nextToken))
				{
					boolean value = jsonReader.nextBoolean();
				}
				else if (JsonToken.NULL.equals(nextToken))
				{
					jsonReader.nextNull();
				}
				else if (JsonToken.BEGIN_ARRAY.equals(nextToken)){
					jsonReader.beginArray();
				}
				else if (JsonToken.END_ARRAY.equals(nextToken)){
					jsonReader.endArray();
				}

					
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				logger.trace("Closing jsonReader");
				jsonReader.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				e.printStackTrace();
			}
		}
		
		logger.warn("The text returned is empty. JSON is:\n "+tweetJson);
		return "";

//		//gson library (see POM.XML for dependency)
//        return JsonParser.parseString(tweetJson)
//                .getAsJsonObject()
//                .get("text")
//                .getAsString();

	}

	/**
	 * Timestamp in milliseconds
	 * 
	 * @param tweetJson
	 * @return
	 */
	public static String getTimestampMsTweet(String tweetJson) {
		// gson library (see POM.XML for dependency)
		return JsonParser.parseString(tweetJson).getAsJsonObject().get("timestamp_ms").getAsString();

	}

	public static String getLangFromTweet(String tweetJson) {
		// gson library (see POM.XML for dependency)
		return JsonParser.parseString(tweetJson).getAsJsonObject().get("lang") // "en" is English, "pt" is Portuguese,
																				// ...
				.getAsString();

	}

}
