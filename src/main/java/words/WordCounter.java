package words;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to count 'words' in a text file.
 * 
 * Specifications are minimal, so assumptions are:
 *  - words are text surrounded by whitespace.
 *  - punctuation is not to be included with words
 *  - dates are to be left as is: 01/01/2000, 01-jun-2000, 01-01-00
 *  - apostrophes in words are ok: i.e "boy's"
 *  - numbers are to be left as is
 *  - external class will call this class, request data and will add own text to results
 *  - no external libs required
 *  - efficient code is requested, doesn't say the fastest
 *  
 * @author PhilN
 */
public class WordCounter {

	private static int DECIMAL_PLACES = 3;
	
	// Longest English word is 45 chars, so maybe faster to use int array to hold count?
	//private int[] wordCountLength = new int[50];
	
	// Holds map of "Word length" -> "Number of times found"
	private Map<Integer,Integer> wordlengthCount = new HashMap<Integer,Integer>();
	
	// Keep running total of words found
	private int wordCount = 0;
	
	// Total length of all words found
	private int totalLength = 0;
	
	// Average length of the words found
	private double averageWordLength = 0.0;
	
	// Count of word length that occurred the most (mode)
	private int modeCount = 0;
	
	// List of modes (modes are the numbers that appear the most in a set of data)
	private List<Integer> modes = new ArrayList<Integer>();
		

	/**
	 * Default constuctor, requires further call to processFile().
	 * 
	 */
	public WordCounter() {
		
	}
	
	
	/**
	 * Constuctor that takes a filename of a text file to parse.
	 * 
	 * @param filename	Name of the file to count words from
	 */
	public WordCounter(String filename) {
		//System.out.println("Filename: " + filename);
		
		if (filename!=null && filename.length()>0) {
			processFile(filename);
		} 
	}
	
	/**
	 * Reads in a file and calculates the number of words, average length and most occurring word lengths.
	 * 
	 * @param filename String of the filename to read in
	 */
	public void processFile(String filename) {
		BufferedReader reader = null;
		
		resetValues();
		
		try {
			reader = new BufferedReader(new FileReader(filename));
			String line = reader.readLine();
			
			while (line != null) {	
				processLine(line);
				line = reader.readLine();
			}	
		} catch (FileNotFoundException e) {
			System.out.println("File: " + filename + " not found.");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}		

		if (wordCount == 0) {
			System.out.println("No words were found.");
		} else {
			// Calculate these now so further calls don't repeat the work
			calcAverageWordLength();
			
			// Find the modes
			findFrequentWordLength();
		}
	}
	

	/**
	 * Reset all values each time a file is to be processed
	 */
	private void resetValues() {
		wordlengthCount.clear();
		wordCount = 0;
		totalLength = 0;
		averageWordLength = 0.0;
		modeCount = 0;
		modes.clear();
	}
	
	
	/**
	 * Splits a String into 'words', removes punctuation characters and counts words.
	 * 
	 * @param line The String of text to count words in
	 */
	private void processLine(String line) {
		
		// Use regex to split on white space (this will also trim)
		String[] split = line.split("(\\s+)");
		
		// For every 'word' found...
		for (String s : split) {

			// Deal with simple words first
			if (!hasDigits(s)) {
				String[] words;
				
				if (!isEmailAddress(s)) {
					// Split on fullstop incase of missing space i.e. "done." or "end.Next"
					words = s.split("\\.");
				} else {
					words = new String[] {s};
				}
				for (String word : words) {
					String singleWord = removePunctuation(word);
					addWordToMap(singleWord);
				}
			} else {
				addWordToMap(removeNumberPunctuation(s));	
			}
		}
	}

	
	/**
	 * Basic email test. Looks for a @ symbol surrounded by characters.
	 * 
	 * @param word The word to check for email format
	 * @return true if the word is an email address, else false
	 */
	private boolean isEmailAddress(String word) {
		return word.matches("^[\\S]+@[\\S]+$");	
	}
	
	/**
	 * Checks if a String contains digits
	 * 
	 * @param word The String to check
	 * @return true If the String contains digits, otherwise false
	 */
	private boolean hasDigits(String word) {
		return word.matches(".*[\\d]+.*");
	}
	
	
	/**
	 * All words found are stored in a map, along with running totals.
	 * 
	 * @param word The word to add to the collection of all words found
	 */
	private void addWordToMap(String word) {
		int wordLength = word.length();
		
		if (wordLength > 0) {
			// Add to map of word length counts
			wordlengthCount.merge(wordLength, 1, Integer::sum);
			
			// Running total of words found
			wordCount++;
			
			// Running total of word lengths
			totalLength += wordLength;
		}
	}
	
	
	/**
	 * Uses regular expressions to remove leading and training punctuation. Won't remove ampersands (special case).
	 * 
	 * @param word	The word to remove leading and trailing punctuation from
	 * @return String The word with punctuation removed, may be an empty string
	 */
	private String removePunctuation(String word) {
		// Leading punctuation
		word = word.replaceAll("^[\\p{Punct}&&[^&]]+", "");
		
		// Trailing punctuation
		word = word.replaceAll("[\\p{Punct}&&[^&]]+$", "");
		
		return word;
	}

	
	/**
	 * Uses regular expressions to remove leading and training punctuation. 
	 * Won't remove leading chars such as -, +, $, £
	 * Won't remove trailing chars such as %
	 * 
	 * @param word	The word to remove leading and trailing punctuation from
	 * @return String The word with punctuation removed, may be an empty string
	 */	
	private String removeNumberPunctuation(String number) {
		// Leading punctuation
		number = number.replaceAll("^[\\p{Punct}&&[^+-]]+", "");
		
		// Trailing punctuation
		number = number.replaceAll("[\\p{Punct}&&[^%]]+$", "");
		
		return number;
	}
	
	
	/**
	 * Calculates the average word length from all those found, to set number of decimal places.
	 */
	private void calcAverageWordLength() {
		// Calc the average
		double average = (double) totalLength/wordCount;
		//System.out.println("Average: " + String.format("%.3f",average));
		
		// Convert to BD so we can set d.p.
		BigDecimal bd = new BigDecimal(average).setScale(DECIMAL_PLACES, RoundingMode.HALF_UP);
		averageWordLength =  bd.doubleValue();
	}
	
	
	/**
	 * Find which word lengths occur the most (i.e. the modal values).
	 */
	private void findFrequentWordLength() {

		wordlengthCount.forEach((k,v) -> {
			if (v.compareTo(modeCount) > 0) {
				// Found new most occuring word length
				modeCount = v;
				// clear any previous word lengths held
				modes.clear();
				// Add this word's length
				modes.add(k);
			} else if (v.compareTo(modeCount) == 0) {
				// Found another word length that occurs the same number of times
				modes.add(k);
			}	
		});	
	}
	
	
	// *** Public API Start ***
	
	/**
	 * Count of the total number of 'words' found in the file.
	 * 
	 * @return int Total number of words found, or 0 if no words found
	 */
	public int getWordCount() {
		return wordCount;
	}
	
	
	/**
	 * Calculates the average word length from all the words founds. (Formatted to a set number of decimal places.)
	 * 
	 * @return double The average word length, or 0 if no words found 
	 */
	public double getAverageWordLength() {
		return averageWordLength;
	}
	
	
	/**
	 * Highest number of times that a length of word that was found.
	 * 
	 * @return int Most times a word length was found, or 0 if no words found
	 */
	public int getWordLengthHighestCount() {
		return modeCount;
	}
	
	
	/**
	 * Returns list of modal word lengths as a String - delimited by ampersand if more than one.
	 * 
	 * @return String List of most frequently occuring word lengths, or an empty String if no words found
	 */
	public String getMostFreqWordLengthsAsString() {
		String delim = "";
		StringBuilder sb = new StringBuilder();

		for(Integer i : modes) {
			sb.append(delim);
			sb.append(i);
			delim = " & ";
		}
		
		return sb.toString();
	}
	
	
	/**
	 * Map of word lengths to count of times found
	 * 
	 * @return Map Returns a Map of "Word length" to "Count of times found"
	 */
	public Map<Integer,Integer> getMapWordLengthCount() {
		return wordlengthCount;
	}
	
	
//	/**
//	 * Option method to get list of most frequently occurring word lengths as an int array
//	 * 
//	 * @return in[] An array containing the most frequently occuring word lengths
//	 */
//	public int[] getMostFreqWordLengths() {
//		int[] array = wordLengthsMostFreq.stream().mapToInt(Integer::intValue).toArray();
//		return array;
//	}


}
