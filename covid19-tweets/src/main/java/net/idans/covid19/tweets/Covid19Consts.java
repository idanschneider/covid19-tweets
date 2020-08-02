package net.idans.covid19.tweets;

public class Covid19Consts {
	
	public final static String TOPIC_NAME_COVID19 = "covid19_tweets";
	
	public final static String ELASTIC_SEARCH_INGESTION_APP_GROUP_ID = "elastic-search-db-ingestion";
	
	
	public final static String KAKFA_TO_ES_GROUP_ID = "kafka-to-es-group";
	public final static String KAKFA_TO_CSV_GROUP_ID = "kafka-to-csv-group";
	
	
	public final static String COVID19_CSV_ID_COLUMN_NAME = "id";
	public final static String COVID19_CSV_TEXT_COLUMN_NAME = "text";
	public final static String COVID19_CSV_LANG_COLUMN_NAME = "lang";
	public final static String COVID19_CSV_TIMESTAMP_MS_COLUMN_NAME = "timestamp_ms";
	public final static String COVID19_CSV_TWEET_JSON_COLUMN_NAME = "tweet_json";
	
	//ELASTIC SEARCH
	public static final String ES_COVID19_TWEETS_INDEX_NAME = "covid19-tweets";
	
	
	public final static String APP_NAME_COVID19_INGESTION = "covid19-ingestion-app";

}
