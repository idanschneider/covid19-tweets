package net.idans.log;

import org.slf4j.Logger;

public class LogUtilities {
	
	public static void logErrorMessage(Logger logger, String message)
	{
		logger.error(message);
		
		//TODO: Write message to Kafka Error log!
		
		
	}

}
