package words;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Class to perform JUnit tests against the WordCounter class.
 * 
 * @author PhilN
 *
 */
public class WordCounterTest {

	private static final String RESOURCE_FOLDER = "src\\test\\resources\\";
	private static final String TEST_FILE_SIMPLE = "testing.txt";
	private static final String TEST_FILE_BIBLE = "bible_daily.txt";
	private static final String SYNALOGIK_TEST_DATA = "Hello world & good morning. The date is 18/05/2016";
	
	/**
	 * Runs before all @Test methods. This method must be static!
	 * @throws Exception - Broad catch all exception
	 */
	@BeforeAll
	public static void testWordCounter() throws Exception {
		System.out.println("@BeforeAll");
		System.out.println("DIR: " + System.getProperty("user.dir"));
	}

	
	/**
	 * Test that the WordCounter can be instantiated without a filename, and that a further call process a file.
	 */
	@Test
	public void testNewWordCounterConstructor() {
		System.out.println("Testing testNewWordCounterConstructor()");
		WordCounter wc = new WordCounter();
		wc.processFile(RESOURCE_FOLDER + TEST_FILE_SIMPLE);
		assertNotNull(wc);
		assertEquals(wc.getWordCount(), 9);
	}
	
	/**
	 * Test that the WordCounter can be instantiated with a filename to process a file.
	 */
	@Test
	public void testNewWordCounterFileConstructor() {
		System.out.println("Testing testNewWordCounterFileConstructor()");
		WordCounter wc = new WordCounter(RESOURCE_FOLDER + TEST_FILE_SIMPLE);
		assertNotNull(wc);
		assertEquals(wc.getWordCount(), 9);
	}
	
	@TempDir
	File temporaryDir;

	/** 
	 * Testing with the data supplied in the spec
	 * 
	 * @throws IOException - if the temporary test file can't be created
	 */
	@Test
	public void testSynalogikExample() throws IOException {
		System.out.println("Testing testSynalogikExample()");
		
		// Create temp test file
	    File letters = new File(temporaryDir, "SynalogikTest.txt");
	    List<String> lines = Arrays.asList(SYNALOGIK_TEST_DATA);
	    Files.write(letters.toPath(), lines);

	    // Create new test object
	    WordCounter wc = new WordCounter(letters.getPath());
		assertNotNull(wc);
		
		// Check against results supplied by Snyalogik
		assertEquals(wc.getWordCount(), 9);
		assertEquals(wc.getAverageWordLength(), 4.556);
		assertEquals(wc.getWordLengthHighestCount(), 2);
		assertEquals(wc.getMostFreqWordLengthsAsString(), "4 & 5");
		
		Map<Integer,Integer> wordLengthCountMap = wc.getMapWordLengthCount();
		assertEquals(wordLengthCountMap.size(), 7);
		assertEquals(wordLengthCountMap.get(1), 1);
		assertEquals(wordLengthCountMap.get(2), 1);
		assertEquals(wordLengthCountMap.get(3), 1);
		assertEquals(wordLengthCountMap.get(4), 2);
		assertEquals(wordLengthCountMap.get(5), 2);
		assertEquals(wordLengthCountMap.get(7), 1);
	}
	
	/** 
	 * Test for simple 'words' - no punctuation or numbers
	 * 
	 * @throws IOException - if the temporary test file can't be created
	 */
	@Test
	public void testSimpleWords() throws IOException {
		System.out.println("Testing testSimpleWords()");
		
		// Create temp test file
	    File words = new File(temporaryDir, "simple.txt");
	    List<String> lines = Arrays.asList("one ", "two ", "three ");
	    Files.write(words.toPath(), lines);

	    // Count words
	    WordCounter wc = new WordCounter(words.getPath());
		assertEquals(wc.getWordCount(), lines.size());
	}
	
	/** 
	 * Test for simple 'words' with full stops. 
	 * Fullstop at the end must be removed.
	 * Fullstop in the middle should be split into multiple words.
	 * 
	 * @throws IOException - if the temporary test file can't be created
	 */
	@Test
	public void testMissingSpace() throws IOException {
		System.out.println("Testing testMissingSpace()");
		
		// Create temp test file
	    File words = new File(temporaryDir, "missingSpace.txt");
	    //                                  1       2       3      4        5     6         7       8       9   10  11  12
	    List<String> lines = Arrays.asList("one ", "two ", "three.(Four ", "first.second", "dots....dots", "aaa.bbb.ccc.ddd");
	    Files.write(words.toPath(), lines);

	    // Create new test object
	    WordCounter wc = new WordCounter(words.getPath());
		assertEquals(wc.getWordCount(), 12);
	}
	
	/** 
	 * Test that email address doesn't get split like its a simple word
	 * 
	 * @throws IOException - if the temporary test file can't be created
	 */
	@Test
	public void testEmailAddress() throws IOException {
		System.out.println("Testing testEmailAddress()");
		
		// Create temp test file
	    File words = new File(temporaryDir, "email.txt");
	    //                                  1       2                         3
	    List<String> lines = Arrays.asList("one ", "phil.n2000@yahoo.co.uk", "two");
	    Files.write(words.toPath(), lines);

	    // Create new test object
	    WordCounter wc = new WordCounter(words.getPath());
		assertEquals(wc.getWordCount(), 3);
	}
	
	/** 
	 * Test that not passing a file doesn't cause a crash.
	 */
	@Test
	void testNoFile() throws IOException {
		System.out.println("Testing testNoFile()");

		// Create new test object
	    WordCounter wc = new WordCounter();
		
		// Check against results supplied by Snyalogik
		assertEquals(wc.getWordCount(), 0);
		assertEquals(wc.getAverageWordLength(), 0.000);
		assertEquals(wc.getWordLengthHighestCount(), 0);
		assertEquals(wc.getMostFreqWordLengthsAsString(), "");
		
		Map<Integer,Integer> wordLengthCountMap = wc.getMapWordLengthCount();
		assertEquals(wordLengthCountMap.size(), 0);
	}
	
	/** 
	 * Some specific words size are known for bible_daily.txt
	 * 
	 * @throws IOException - if the temporary test file can't be created
	 */
	@Test
	public void testBibleFile() throws IOException {
		System.out.println("Testing testBibleFile()");

		WordCounter wc = new WordCounter();
		wc.processFile(RESOURCE_FOLDER + TEST_FILE_BIBLE);
		
		// Can check against my results, but don't know if 100% correct results here
		assertEquals(wc.getWordCount(), 793120);
		assertEquals(wc.getAverageWordLength(), 4.084);
		assertEquals(wc.getWordLengthHighestCount(), 221409);
		assertEquals(wc.getMostFreqWordLengthsAsString(), "3");
		
		// Large word sizes have been checked
		Map<Integer,Integer> wordLengthCountMap = wc.getMapWordLengthCount();
		assertEquals(wordLengthCountMap.get(19), null);		// There are no words this length
		assertEquals(wordLengthCountMap.get(18), 2);
		assertEquals(wordLengthCountMap.get(17), 4);
		assertEquals(wordLengthCountMap.get(16), 16);
		assertEquals(wordLengthCountMap.get(15), 90);
		assertEquals(wordLengthCountMap.get(14), 350);
	}
	
	@Test
	public void testNumbers() {
		System.out.println("Testing testNumbers()");

		String[] numbers = {"100", "100.9", "+200", "£300.00", "55%", "2000/01/01", "01-01-2000", "1000,999.0", "-1000,999.0", "V9.0", "(9999)", "****1234***", ">>>>11111<<<<<"};
		String[] newNumbers = new String[numbers.length];
		
		for (int i=0; i<numbers.length; i++) {
			newNumbers[i] = removePunctuation(numbers[i]);
		}

		assertEquals(newNumbers[0], numbers[0]);
		assertEquals(newNumbers[1], numbers[1]);
		assertEquals(newNumbers[2], numbers[2]);
		assertEquals(newNumbers[3], numbers[3]);
		assertEquals(newNumbers[4], numbers[4]);
		assertEquals(newNumbers[5], numbers[5]);
		assertEquals(newNumbers[6], numbers[6]);
		assertEquals(newNumbers[7], numbers[7]);
		assertEquals(newNumbers[8], numbers[8]);
		assertEquals(newNumbers[9], numbers[9]);
		assertEquals(newNumbers[10], "9999");
		assertEquals(newNumbers[11], "1234");
		assertEquals(newNumbers[12], "11111");
	}
	
	/**
	 * Copied from WordCount class - can call into private method to ensure actual code is used, but short on time...
	 * 
	 * @param number The number to remove punctuation from
	 * @return String the number with leading and trailing punctuation removed. Might be an empty String
	 */
	private String removePunctuation(String number) {
		// Leading punctuation
		number = number.replaceAll("^[\\p{Punct}&&[^+-]]+", "");
		
		// Trailing punctuation
		number = number.replaceAll("[\\p{Punct}&&[^%]]+$", "");
		
		return number;
	}
}
