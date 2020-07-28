package net.idans.covid19.tweets.spark;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class SparkUtilities {
	
public static Set<String> borings = new HashSet<String>();
	
	static {
		// On the video, I say "change this class name" - ignore that.
		// The class name should have been Util, as it is here!
		//Note: when packing the JAR, words-to-filter is in the root of the jar, that's why
		//the path here is at the root
		InputStream is = SparkUtilities.class.getResourceAsStream("/words-to-filter.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		br.lines().forEach(borings::add);
	}

	/**
	 * Returns true if we think the word is "boring" - ie it doesn't seem to be a keyword
	 * for a training course.
	 */
	public static boolean isBoring(String word)
	{
		return borings.contains(word);
	}

	/**
	 * Convenience method for more readable client code
	 */
	public static boolean isNotBoring(String word)
	{
		return !isBoring(word);
	}

}
