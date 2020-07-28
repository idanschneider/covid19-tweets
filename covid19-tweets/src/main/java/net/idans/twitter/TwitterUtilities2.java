//package net.idans.twitter;
//
//import java.io.IOException;
//import java.io.StringReader;
//import java.util.Arrays;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Set;
//import java.util.concurrent.BlockingQueue;
//
//import org.spark_project.guava.collect.Lists;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//import com.google.gson.stream.JsonReader;
//import com.google.gson.stream.JsonToken;
//import com.twitter.hbc.ClientBuilder;
//import com.twitter.hbc.core.Client;
//import com.twitter.hbc.core.Constants;
//import com.twitter.hbc.core.Hosts;
//import com.twitter.hbc.core.HttpHosts;
//import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
//import com.twitter.hbc.core.processor.StringDelimitedProcessor;
//import com.twitter.hbc.httpclient.auth.Authentication;
//import com.twitter.hbc.httpclient.auth.OAuth1;
//
//public class TwitterUtilities2 {
//
//	final static String consumerKey = "kNXhXUKoMcOmngmm1uMNIjJFz";
//	final static String consumerSecret = "XHZ66XuUsvAMhR0yj5hMqFPbM1DS7cD2QRcMIohBtKfoXGOqVR";
//	final static String token = "14647841-gxx9fiTqOvoBRXXvq4Lb46URCedxE739Tg1BrVrie";
//	final static String secret = "L3MzYZQkqKHpjoMbwEyc0UoHlxQ8AVXEMyvtHIZp9kE72";
//
//	public static Client createTwitterStreamingClient(BlockingQueue<String> msgQueue, List<String> terms,
//			List<String> languages) {
//		/**
//		 * Declare the host you want to connect to, the endpoint, and authentication
//		 * (basic auth or oauth)
//		 */
//		Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
//		StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
//		// Optional: set up some followings and track terms
//		// List<Long> followings = Lists.newArrayList(1234L, 566788L);
//		hosebirdEndpoint.languages(languages); // filter only english tweets
//
//		// hosebirdEndpoint.followings(followings);
//		hosebirdEndpoint.trackTerms(terms);
//
//		// // These secrets should be read from a config file
//		Authentication hosebirdAuth = new OAuth1(consumerKey, consumerSecret, token, secret);
//
//		ClientBuilder builder = new ClientBuilder().name("Hosebird-Client-01") // optional: mainly for the logs
//				.hosts(hosebirdHosts).authentication(hosebirdAuth).endpoint(hosebirdEndpoint)
//				.processor(new StringDelimitedProcessor(msgQueue));
//		// .eventMessageQueue(eventQueue); // optional: use this if you want to process
//		// client events
//
//		Client hosebirdClient = builder.build();
//		return hosebirdClient;
//
//	}
//
//	public static String getIdStrFromTweet(String tweetJson) {
//
//		// gson library (see POM.XML for dependency)
//		return JsonParser.parseString(tweetJson).getAsJsonObject().get("id_str").getAsString();
//
//	}
//
//	public static boolean isExtendedTweet(String tweetJson)
//	{
//		String searchedKey = "extended_tweet";
//		JsonObject object = JsonParser.parseString(tweetJson).getAsJsonObject();
//		System.out.println(tweetJson);
//		return keyExists(object, searchedKey);
//		
//	}
//	
//	public static boolean isRetweet(String tweetJson)
//	{
//		String tweetText = JsonParser.parseString(tweetJson).getAsJsonObject().get("text").getAsString();
//		if (tweetText.startsWith("RT"))
//		{
//			return true;
//		}
//		return false;
//	}
//	
//	public static boolean isQuoted(String tweetJson)
//	{
//		JsonElement elem = JsonParser.parseString(tweetJson).getAsJsonObject().get("quoted_status");
//		if (elem==null)
//		{
//			return false;
//		}
//		else
//		{
//			return true;
//		}
//		
//	}
//	
//	
//
//
//	private static boolean keyExists(JsonObject object, String searchedKey) {
//		
//		//System.out.println("Ni");
//		boolean exists = object.has(searchedKey);
//		if (exists == true)
//		{
//			//System.out.println("TRUE");
//			return true;
//		}
//		//else
//		
//		Set<String> keys = object.keySet();
//		Iterator<String> keysIterator = keys.iterator();
//		while (keysIterator.hasNext()) {
//			String key = (String) keysIterator.next();
//			if (object.get(key) instanceof JsonObject) {
//				exists = keyExists((JsonObject)object.get(key), searchedKey);
//				if (exists == true)
//				{
////					System.out.println("TRUE");
//					return true;
//				}
//			}
//		}
//		
//		return false;
//
//	}
//	
//	public static String getTextFromTweet(String tweetJson) {
//		boolean isTweetExtended = isExtendedTweet(tweetJson);
//		boolean isRetweet = isRetweet(tweetJson);
//		boolean isQuotedStatus = isQuoted(tweetJson);
//		
//		if (isTweetExtended == true && isQuotedStatus==true)
//		{
//			//disregard the quoted tweet. Take the original tweet
//			return getTextFromTweetRegular(tweetJson, false);
//			
//		}
//		else if (isTweetExtended == true && isRetweet==false)
//		{
////			System.out.println("****************=============================*********************");
//			//get the extended_tweet json
//			String extendedTweetString = JsonParser.parseString(tweetJson)
//											.getAsJsonObject()
//											.get("extended_tweet").toString();
//
//			return getTextFromTweetRegular(extendedTweetString, true);
//			
//		}
//		else if (isTweetExtended == true && isRetweet==true)
//		{
////			System.out.println("****************=============================*********************");
//			//get the extended_tweet json
//			String extendedTweetString = JsonParser.parseString(tweetJson)
//					.getAsJsonObject()
//					.get("retweeted_status")
//					.getAsJsonObject()
//					.get("extended_tweet").toString();
//
//			return getTextFromTweetRegular(extendedTweetString, true);
//			
//		}
//		else
//		{
//			return getTextFromTweetRegular(tweetJson, false);
//		}
//		
//		
//	}
//
//	public static String getTextFromTweetRegular(String tweetJson, boolean isTweetExtended) {
//		// I needed to read the TEXT field from the tweetJson with a jsonReader (instead
//		// of jsonParser)
//		// because of errors I got claiming the text was malformed...
//
//		JsonReader jsonReader = new JsonReader(new StringReader(tweetJson));
//		jsonReader.setLenient(true); // <-- had to add this to avoid "Use JsonReader.setLenient(true) to accept
//										// malformed JSON "
//		boolean readNextValue = false;
////		boolean isTweetExtended = isExtendedTweet(tweetJson);
////		if (isTweetExtended == true)
////		{
////			System.out.println("****************=============================*********************");
////		}
//
//		try {
//			//while (jsonReader.hasNext()) {
//			while (jsonReader.hasNext()) {
//				//System.out.println("next");
//				JsonToken nextToken = jsonReader.peek();
//				if (nextToken == JsonToken.END_ARRAY || nextToken == JsonToken.END_OBJECT)
//				{
//					System.out.println("End of array or end of object "+nextToken);
//					break;
//				}
//		        //System.out.println("TOKEN " +nextToken);
//
//				if (JsonToken.BEGIN_OBJECT.equals(nextToken)) {
//
//					jsonReader.beginObject();
//
//				} 
//				else if (JsonToken.END_OBJECT.equals(nextToken)) {
//					jsonReader.endObject();
//				}
//
//				
//				else if (JsonToken.NAME.equals(nextToken)) {
//
//					String name = jsonReader.nextName();
//					if (name.equalsIgnoreCase("text") && isTweetExtended == false) {
//						readNextValue = true;
//					}
//					if (name.equalsIgnoreCase("full_text") && isTweetExtended == true) {
//						readNextValue = true;
//					}
////		            System.out.println(name);
//
//				} else if (JsonToken.STRING.equals(nextToken)) {
//
//					String value = jsonReader.nextString();
////		            System.out.println(value);
//					if (readNextValue) {
//		            	System.out.println("---------isTweetExtended="+isTweetExtended+" and returning the above value "+value);
//						return value;
//					}
//
//				} else if (JsonToken.NUMBER.equals(nextToken)) {
//
//					long value = jsonReader.nextLong();
////		            System.out.println(value);
//
//				}
//				else if (JsonToken.BOOLEAN.equals(nextToken))
//				{
//					boolean value = jsonReader.nextBoolean();
//					//System.out.println("Tada");
//				}
//				else if (JsonToken.NULL.equals(nextToken))
//				{
//					jsonReader.nextNull();
//					//System.out.println("Tada2");
//				}
//				else if (JsonToken.BEGIN_ARRAY.equals(nextToken)){
//					jsonReader.beginArray();
//				}
//				else if (JsonToken.END_ARRAY.equals(nextToken)){
//					jsonReader.endArray();
//				}
//
//					
//				
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			try {
////				System.out.println("Closing jsonReader");
//				jsonReader.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		
//		System.out.println("EMPTYYY");
//		return "";
//
////		//gson library (see POM.XML for dependency)
////        return JsonParser.parseString(tweetJson)
////                .getAsJsonObject()
////                .get("text")
////                .getAsString();
//
//	}
//
//	/**
//	 * Timestamp in milliseconds
//	 * 
//	 * @param tweetJson
//	 * @return
//	 */
//	public static String getTimestampMsTweet(String tweetJson) {
//		// gson library (see POM.XML for dependency)
//		return JsonParser.parseString(tweetJson).getAsJsonObject().get("timestamp_ms").getAsString();
//
//	}
//
//	public static String getLangFromTweet(String tweetJson) {
//		// gson library (see POM.XML for dependency)
//		return JsonParser.parseString(tweetJson).getAsJsonObject().get("lang") // "en" is English, "pt" is Portuguese,
//																				// ...
//				.getAsString();
//
//	}
//
//}
