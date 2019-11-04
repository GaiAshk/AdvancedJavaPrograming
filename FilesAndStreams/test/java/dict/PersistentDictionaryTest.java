package dict;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test a persistent dictionary.
 * 
 * This test is abstract, and must be subclassed to test for a concrete
 * PersistentDictionary.
 * 
 * @author talm
 * 
 */
public abstract class PersistentDictionaryTest {

	public final static int NUM_EXTENDED = 32; // number of extended tests
	public final static int MAX_SIZE = 257; // maximum number of definitions.
	public final static int MAX_LEN = 100; // maximum length of word/definition
	public final static char[] CHARS = "abcdefghijklmnopqstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ \t/1234567890!@#$%^&*()-=_+{}[]\";?><,.~`"
			.toCharArray();

	Random rnd;

	@Before
	public void setup() {
		rnd = new Random(19); // Fixed seed so tests will be repeatable.
	}

	/**
	 * Return a persistent dictionary. If one doesn't exist by that name, create
	 * it. Subclasses should override this method to return a concrete
	 * PersistentDictionary.
	 * 
	 */
	abstract PersistentDictionary getDictionary(File file) throws IOException;

	File dictFile;

	public PersistentDictionaryTest() {
		try {
			dictFile = File.createTempFile("DictTest", ".tmp");
		} catch (IOException e) {
			Assert.fail("Couldn't create temporary file for testing");
		}
	}

	String[] testWords = { "Second", "First", "CAB", "BBA", "", "1", "4" };

	String[] testDefs = { "Some definition", "another definition", "",
			"yet another definition", "lorem ipsum dolor", "",
			"- ? d  ddd cccc e" };

	String[] addWords = { "Second", "Third", "AAAA", "CAB" };

	String[] addDefs = { "A completely different definition", "1 2 3", "BBBB",
			"yahoo!", };

	/**
	 * Generate an array of random strings.
	 * 
	 * @param numStrings
	 *            number of strings in array
	 * @param maxLength
	 *            maximum length of each string.
	 * @return
	 */
	String[] randomStrings(int numStrings, int maxLength) {
		String[] strs = new String[numStrings];

		for (int i = 0; i < strs.length; ++i) {
			int len = rnd.nextInt(maxLength);
			char chars[] = new char[len];
			for (int j = 0; j < chars.length; ++j) {
				chars[j] = CHARS[rnd.nextInt(CHARS.length)];
			}
			strs[i] = new String(chars);
		}
		return strs;
	}

	/**
	 * Fill a dictionary with words. Any class with a "put(String,String)"
	 * method is supported as a dictionary.
	 * 
	 * @param dict
	 *            the dictionary to be filled
	 * @param words
	 *            the array of words that will fill it
	 * @param defs
	 *            the definitions corresponding to the words (must have the same
	 *            length as words).
	 * @throws Exception
	 *             may throw a reflection-related exception if dict is not a
	 *             valid dictionary or an IOException if dict.put does.
	 */
	void fillMap(Object dict, String[] words, String[] defs) throws Exception {
		Class<?> dictClass = dict.getClass();

		Method put = null;

		// Go over all the methods of dict and see if there is a put(*,*) method
		// for which both arguments can be assigned Strings. (this matches
		// (put(Object,Object) and put(String,String)).
		for (Method m : dictClass.getMethods()) {
			if (!m.getName().equals("put"))
				continue;
			Class<?>[] params = m.getParameterTypes();
			if (params.length == 2 && params[0].isAssignableFrom(String.class)
					&& params[1].isAssignableFrom(String.class)) {
				put = m;
				break;
			}
		}
		if (put == null)
			throw new NoSuchMethodException();

		for (int i = 0; i < words.length; ++i) {
			put.invoke(dict, words[i], defs[i]);
		}
	}

	/**
	 * Compare a Java map to a dictionary. Checks that the sizes are the same,
	 * then iterates over all keys in the map and compares the values.
	 * 
	 * @param map
	 * @param dict
	 * @return
	 * @throws IOException
	 */
	void testEquality(String msg, Map<String, String> map,
			PersistentDictionary dict) throws IOException {
		assertEquals(msg + ": Map and dictionary have different sizes",
				map.size(), dict.size());

		for (String key : map.keySet()) {
			assertEquals(msg + ": Recall failed for key " + key, map.get(key),
					dict.get(key));
		}
	}

	/**
	 * Test that the dictionary can recall words that were stored in it.
	 * 
	 * @throws Exception
	 */
	@Test
	public void fullRecallTest() throws Exception {
		TreeMap<String, String> expected = new TreeMap<String, String>();
		fillMap(expected, testWords, testDefs);

		dictFile.delete();
		PersistentDictionary dict = getDictionary(dictFile);
		assertNotNull(dict);

		dict.open();
		fillMap(dict, testWords, testDefs);

		// In memory test
		testEquality("In-memory recall", expected, dict);

		dict.close();

		// Persistence test
		dict = getDictionary(dictFile);
		dict.open();

		testEquality("Persistent recall", expected, dict);

		// Combined test
		fillMap(expected, addWords, addDefs);
		fillMap(dict, addWords, addDefs);

		testEquality("Combined recall", expected, dict);

		dict.close();
		dictFile.delete();
	}

	/**
	 * Test that the dictionary can recall words that were stored in it.
	 * 
	 * @throws Exception
	 */
	@Test
	public void fullRecallInMemoryExtendedTest() throws Exception {
		TreeMap<String, String> expected = new TreeMap<String, String>();
		for (int i = 0; i < NUM_EXTENDED; ++i) {
			expected.clear();
			int numWords = rnd.nextInt(MAX_SIZE);

			String[] testWords = randomStrings(numWords, MAX_LEN);
			String[] testDefs = randomStrings(numWords, MAX_LEN);
			fillMap(expected, testWords, testDefs);

			dictFile.delete();
			PersistentDictionary dict = getDictionary(dictFile);
			assertNotNull(dict);

			dict.open();
			fillMap(dict, testWords, testDefs);

			// In memory test
			testEquality("In-memory recall extended", expected, dict);

			dict.close();
		}
		dictFile.delete();
	}

	/**
	 * Test that the dictionary can recall words that were stored in it after
	 * closing and opening. Note that this test will almost certainly fail if
	 * the in-memory test fails.
	 * 
	 * @throws Exception
	 */
	@Test
	public void fullRecallPersistentExtendedTest() throws Exception {
		TreeMap<String, String> expected = new TreeMap<String, String>();

		for (int i = 0; i < NUM_EXTENDED; ++i) {
			expected.clear();
			int numWords = rnd.nextInt(MAX_SIZE);

			String[] testWords = randomStrings(numWords, MAX_LEN);
			String[] testDefs = randomStrings(numWords, MAX_LEN);

			fillMap(expected, testWords, testDefs);

			dictFile.delete();
			PersistentDictionary dict = getDictionary(dictFile);
			assertNotNull(dict);

			dict.open();
			fillMap(dict, testWords, testDefs);
			dict.close();

			// Persistence test
			dict = getDictionary(dictFile);
			dict.open();

			testEquality("Persistent recall extended", expected, dict);

			dict.close();
		}
		dictFile.delete();
	}

	/**
	 * Test that the dictionary can recall words that were stored in it.
	 * 
	 * @throws Exception
	 */
	@Test
	public void fullRecallCombinedExtendedTest() throws Exception {
		TreeMap<String, String> expected = new TreeMap<String, String>();
		for (int i = 0; i < NUM_EXTENDED; ++i) {
			expected.clear();
			int numWords = rnd.nextInt(MAX_SIZE);

			String[] testWords = randomStrings(numWords, MAX_LEN);
			String[] testDefs = randomStrings(numWords, MAX_LEN);

			fillMap(expected, testWords, testDefs);

			dictFile.delete();
			PersistentDictionary dict = getDictionary(dictFile);
			assertNotNull(dict);

			dict.open();
			fillMap(dict, testWords, testDefs);

			dict.close();

			// Persistence test
			dict = getDictionary(dictFile);
			dict.open();
			
			int numAddWords = rnd.nextInt(MAX_SIZE);
			String[] addWords = randomStrings(numAddWords, MAX_LEN);
			String[] addDefs = randomStrings(numAddWords, MAX_LEN);

			// Combined test
			fillMap(expected, addWords, addDefs);
			fillMap(dict, addWords, addDefs);

			testEquality("Combined recall extended", expected, dict);

			dict.close();
		}
		dictFile.delete();
	}

	/**
	 * Check that elements disappear after we remove them.
	 * 
	 * @throws Exception
	 */
	@Test
	public void removeElementsInMemoryTest() throws Exception {
		TreeMap<String, String> expected = new TreeMap<String, String>();
		fillMap(expected, testWords, testDefs);

		dictFile.delete();
		PersistentDictionary dict = getDictionary(dictFile);
		assertNotNull(dict);

		dict.open();
		fillMap(dict, testWords, testDefs);
		for (String word : addWords) {
			expected.remove(word);
			dict.remove(word);
		}

		// In memory test
		testEquality("In-memory remove", expected, dict);

		dict.close();
		dictFile.delete();
	}

	@Test
	public void removeElementsPersistentTest() throws Exception {
		TreeMap<String, String> expected = new TreeMap<String, String>();
		fillMap(expected, testWords, testDefs);

		dictFile.delete();
		PersistentDictionary dict = getDictionary(dictFile);
		assertNotNull(dict);

		dict.open();
		fillMap(dict, testWords, testDefs);
		dict.close();

		// Persistence test
		dict = getDictionary(dictFile);
		dict.open();

		for (String word : addWords) {
			expected.remove(word);
			dict.remove(word);
		}

		dict.close();
		dict = getDictionary(dictFile);
		dict.open();

		testEquality("Persistent remove", expected, dict);

		dict.close();
		dictFile.delete();
	}

	@Test
	public void removeElementsPersistentExtendedTest() throws Exception {
		TreeMap<String, String> expected = new TreeMap<String, String>();
		for (int i = 0; i < NUM_EXTENDED; ++i) {
			expected.clear();
			int numWords = rnd.nextInt(MAX_SIZE);

			String[] testWords = randomStrings(numWords, MAX_LEN);
			String[] testDefs = randomStrings(numWords, MAX_LEN);

			fillMap(expected, testWords, testDefs);

			dictFile.delete();
			PersistentDictionary dict = getDictionary(dictFile);
			assertNotNull(dict);

			dict.open();
			fillMap(dict, testWords, testDefs);
			dict.close();

			// Persistence test
			dict = getDictionary(dictFile);
			dict.open();

			for (String word : testWords) {
				if (rnd.nextDouble() < 0.5) {
					expected.remove(word);
					dict.remove(word);
				}
			}
				
			dict.close();
			dict = getDictionary(dictFile);
			dict.open();

			testEquality("Persistent remove extended", expected, dict);

			dict.close();
		}
		dictFile.delete();
	}

	@Test
	public void clearTest() throws Exception {
		TreeMap<String, String> expected = new TreeMap<String, String>();
		fillMap(expected, testWords, testDefs);

		dictFile.delete();
		PersistentDictionary dict = getDictionary(dictFile);
		assertNotNull(dict);

		dict.open();
		fillMap(dict, testWords, testDefs);
		dict.close();

		// Filled dictionary and closed, now we open and clear.
		dict = getDictionary(dictFile);
		dict.open();

		dict.clear();
		expected.clear();

		fillMap(expected, addWords, addDefs);
		fillMap(dict, addWords, addDefs);

		// Check that in-memory everything looks good.

		testEquality("In-memory comparison after clear()", expected, dict);

		dict.close();

		// Check that after persisting the comparison is good.
		dict = getDictionary(dictFile);
		dict.open();
		// Check that in-memory everything looks good.
		testEquality("Persistent comparison after clear()", expected, dict);

		dict.close();
		dictFile.delete();

	}
	

	@Test
	public void extendedCycleTest() throws Exception {
		TreeMap<String, String> expected = new TreeMap<String, String>();
		dictFile.delete();
		PersistentDictionary dict = getDictionary(dictFile);
		
		for (int i = 0; i < NUM_EXTENDED; ++i) {
			// Add some words
			int numWords = rnd.nextInt(MAX_SIZE);

			String[] testWords = randomStrings(numWords, MAX_LEN);
			String[] testDefs = randomStrings(numWords, MAX_LEN);

			fillMap(expected, testWords, testDefs);
			fillMap(dict, testWords, testDefs);
			dict.close();
			dict = getDictionary(dictFile);
			dict.open();
			
			// Delete some words
			for (String word : testWords) {
				if (rnd.nextDouble() < 0.5) {
					expected.remove(word);
					dict.remove(word);
				}
			}
			dict.close();
			dict = getDictionary(dictFile);
			dict.open();
		}

		// Check that in-memory everything looks good.
		testEquality("Multiple add/remove, open/close cycles", expected, dict);
	}

}
