package words;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple class to test the main WordCounter class and display some results.
 * 
 * Made the assumption that API caller would add the text like "Word Count =" so that it can be implemented in other languages etc.
 * 
 * @author PhilN
 *
 */
public class AppRunner {

	public static void main(String[] args) {

		WordCounter wc = new WordCounter(args[0]);
		
		System.out.println("Word count = " + wc.getWordCount());
		System.out.println("Average word length = " + wc.getAverageWordLength());
		
		// Can cast to TreeMap if ordering must be guaranteed
		Map<Integer,Integer> mapWordLength = (HashMap<Integer,Integer>) wc.getMapWordLengthCount();
		mapWordLength.forEach((k,v) -> {
			System.out.println("Number of words of length " + k + " is " + v);
		}); 
		
		System.out.println("The most frequently occurring word length is " + wc.getHighestWordLengthCount() + ", for word lengths of " + wc.getMostFreqWordLengthsAsString());
		
		System.out.println("\nOr, more correctly:");
		System.out.println("The most frequently occurring word length(s) of " + wc.getMostFreqWordLengthsAsString() + ", occurred " +  wc.getHighestWordLengthCount() + " times");;
	}

}
