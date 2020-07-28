package net.idans.json;

public class JSONUtilities {

	public static String normalizeJsonString(String value) {
		
		//To avoid this error: Illegal unquoted character ((CTRL-CHAR, code 10)): has to be escaped using backslash to be included in string value
		//need to add escape character
		//example of problematic text:
		//RT @AskAnshul: Sharad Pawar said that Some people think Covid-19 will go away by building temple.\n\nMeanwhile, Maharashtra govt approves pur\u2026","source":"\u003ca href=\"http:\/\/twitter.com\/download\/android\" rel=\"nofollow\"\u003eTwitter for Android\u003c\/a\u003e","truncated":false,"in_reply_to_status_id":null,"in_reply_to_status_id_str":null,"in_reply_to_user_id":null,"in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,"user":{"id":294077403,"id_str":"294077403","name":"anuj kumar pandey","screen_name":"anujkmrpandey1","location":"\u0928\u0908 \u0926\u093f\u0932\u094d\u0932\u0940, \u092d\u093e\u0930\u0924","url":null,"description":"Young Indian","translator_type":"none","protected":false,"verified":false,"followers_count":5,"friends_count":160,"listed_count":0,"favourites_count":2803,"statuses_count":499,"created_at":"Fri May 06 13:57:22 +0000 2011","utc_offset":null,"time_zone":null,"geo_enabled":false,"lang":null,"contributors_enabled":false,"is_translator":false,"profile_background_color":"C0DEED","profile_background_image_url":"http:\/\/abs.twimg.com\/images\/themes\/theme1\/bg.png","profile_background_image_url_https":"https:\/\/abs.twimg.com\/images\/themes\/theme1\/bg.png","profile_background_tile":false,"profile_link_color":"1DA1F2","profile_sidebar_border_color":"C0DEED","profile_sidebar_fill_color":"DDEEF6","profile_text_color":"333333","profile_use_background_image":true,"profile_image_url":"http:\/\/abs.twimg.com\/sticky\/default_profile_images\/default_profile_normal.png","profile_image_url_https":"https:\/\/abs.twimg.com\/sticky\/default_profile_images\/default_profile_normal.png","default_profile":true,"default_profile_image":false,"following":null,"follow_request_sent":null,"notifications":null},"geo":null,"coordinates":null,"place":null,"contributors":null,"retweeted_status":{"created_at":"Mon Jul 20 06:08:13 +0000 2020","id":1285094139772596224,"id_str":"1285094139772596224","text":"Sharad Pawar said that Some people think Covid-19 will go away by building temple.\n\nMeanwhile, Maharashtra govt app\u2026 https:\/\/t.co\/MWPoTzlBN6","source":"\u003ca href=\"http:\/\/twitter.com\/download\/android\" rel=\"nofollow\"\u003eTwitter for Android\u003c\/a\u003e","truncated":true,"in_reply_to_status_id":null,"in_reply_to_status_id_str":null,"in_reply_to_user_id":null,"in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,"user":{"id":318673863,"id_str":"318673863","name":"Anshul Saxena","screen_name":"AskAnshul","location":"India","url":"http:\/\/www.facebook.com\/AskAnshul","description":"| News Junkie | Politics | Foreign Affairs | National Security | Observer & Analyst | i tweet informative facts and opinions | http:\/\/Youtube.com\/AskAnshul","translator_type":"regular","protected":false,"verified":true,"followers_count":669783,"friends_count":375,"listed_count":502,"favourites_count":12964,"statuses_count":36347,"created_at":"Thu Jun 16 21:26:09 +0000 2011","utc_offset":null,"time_zone":null,"geo_enabled":false,"lang":null,"contributors_enabled":false,"is_translator":false,"profile_background_color":"C0DEED","profile_background_image_url":"http:\/\/abs.twimg.com\/images\/themes\/theme1\/bg.png","profile_background_image_url_https":"https:\/\/abs.twimg.com\/images\/themes\/theme1\/bg.png","profile_background_tile":true,"profile_link_color":"0084B4","profile_sidebar_border_color":"FFFFFF","profile_sidebar_fill_color":"DDEEF6","profile_text_color":"333333","profile_use_background_image":true,"profile_image_url":"http:\/\/pbs.twimg.com\/profile_images\/952822845452599296\/UM8VF4Bt_normal.jpg","profile_image_url_https":"https:\/\/pbs.twimg.com\/profile_images\/952822845452599296\/UM8VF4Bt_normal.jpg","profile_banner_url":"https:\/\/pbs.twimg.com\/profile_banners\/318673863\/1431165340","default_profile":false,"default_profile_image":false,"following":null,"follow_request_sent":null,"notifications":null},"geo":null,"coordinates":null,"place":null,"contributors":null,"is_quote_status":false,"extended_tweet":{"full_text":"Sharad Pawar said that Some people think Covid-19 will go away by building temple.\n\nMeanwhile, Maharashtra govt approves purchase of 6 luxury cars worth Rs 1.37 Crore\n\nBut 40 Kerala doctors who had come to Mumbai at the request of state govt, haven't received their salaries yet.","display_text_range":[0,279],"entities":{"hashtags":[],"urls":[],"user_mentions":[],"symbols":[]}},"quote_count":62,"reply_count":124,"retweet_count":2738,"favorite_count":9768,"entities":{"hashtags":[],"urls":[{"url":"https:\/\/t.co\/MWPoTzlBN6","expanded_url":"https:\/\/twitter.com\/i\/web\/status\/1285094139772596224","display_url":"twitter.com\/i\/web\/status\/1\u2026","indices":[117,140]}],"user_mentions":[],"symbols":[]},"favorited":false,"retweeted":false,"filter_level":"low","lang":"en"},"is_quote_status":false,"quote_count":0,"reply_count":0,"retweet_count":0,"favorite_count":0,"entities":{"hashtags":[],"urls":[],"user_mentions":[{"screen_name":"AskAnshul","name":"Anshul Saxena","id":318673863,"id_str":"318673863","indices":[3,13]}],"symbols":[]},"favorited":false,"retweeted":false,"filter_level":"low","lang":"en","timestamp_ms":"1595232099108"}

		//1: Replace escaped characters
		String newValue = escape(value);
		
		//TODO 2 anything else?
		newValue = newValue.trim();
		return newValue;
	}
	
	
	
	private static String escape(String raw) {
	    String escaped = raw;
	    escaped = escaped.replace("\\", "\\\\");
	    escaped = escaped.replace("\"", "\\\"");
	    escaped = escaped.replace("\b", "\\b");
	    escaped = escaped.replace("\f", "\\f");
	    escaped = escaped.replace("\n", "\\n");
	    escaped = escaped.replace("\r", "\\r");
	    escaped = escaped.replace("\t", "\\t");
	    // TODO: escape other non-printing characters using uXXXX notation
	    return escaped;
	}
	
	/**
	 * Trim whitespaces, newlines etc
	 * @param jSon
	 * @return
	 */
	public static String trim(String jSon) {
		return jSon.trim();
	}
	
	

}
