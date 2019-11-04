package util;

import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.util.*;

import static org.junit.Assert.*;


public class RegexpPracticeTest {
    RegexpPracticeInterface reg;

    @Before
    public void setUp() throws Exception {
        reg = new RegexpPractice();
    }

    @Test
	public void testFindSingleQuotedTextSimple() {
		String[] inputs = {"fdsaf'test'fdsafdsa", "'onetwothree'fourfive", "abc'def'xyz'123'", "more'testing", "''", "'", "another''test" };
		String[] expect = {"test",                "onetwothree",           "def",              null,           "",   null, "" };
		
		for (int i = 0; i < inputs.length; ++i) {
			String output = reg.findSingleQuotedTextSimple(inputs[i]);
			assertEquals(expect[i], output);
		}
	}
	
	@Test
	public void testFindDoubleQuotedTextSimple() {
		String[] inputs = {"fdsaf\"test\"fdsafdsa", "\"onetwothree\"fourfive", "abc\"def\"xyz\"123'", "more\"testing", "\"\"", "\"", "another\"\"test" };
		String[] expect = {"test",                "onetwothree",           "def",              null,           "",   null, "" };
		
		for (int i = 0; i < inputs.length; ++i) {
			String output = reg.findDoubleQuotedTextSimple(inputs[i]);
			assertEquals(expect[i], output);
		}
	}
	
	@Test
	public void testDoubleOrSingleQuoted() {
		ArrayList<String> inputs = new ArrayList<String>();
		ArrayList<List<String>> expect = new ArrayList<List<String>>();
		
		inputs.add("'single'"); expect.add(Arrays.asList("single"));
		inputs.add("more'single'"); expect.add(Arrays.asList("single"));
		inputs.add("'single'more"); expect.add(Arrays.asList("single"));
		inputs.add("\"double\""); expect.add(Arrays.asList("double"));
		inputs.add("more\"double\""); expect.add(Arrays.asList("double"));
		inputs.add("\"double\"more"); expect.add(Arrays.asList("double"));
		inputs.add("\"double\"'single'"); expect.add(Arrays.asList("double", "single"));
		inputs.add("'single'\"double\""); expect.add(Arrays.asList("single", "double"));
		inputs.add("'\"dquoted\"'"); expect.add(Arrays.asList("\"dquoted\""));
		inputs.add("\"'squoted'\""); expect.add(Arrays.asList("'squoted'"));
		inputs.add("\"yes'\"no'maybe'"); expect.add(Arrays.asList("yes'", "maybe"));
		inputs.add("'yes\"'no\"maybe\""); expect.add(Arrays.asList("yes\"", "maybe"));

		for (int i = 0; i < inputs.size(); ++i) {
			List<String> output = reg.findDoubleOrSingleQuoted(inputs.get(i));
			assertEquals(String.format("Test %d failed: Search <<%s>>", i, inputs.get(i)), expect.get(i), output);
		}
	}
	
	
	void fillSingleQuotedTestInputs(List<String> inputs, List<List<String>> expect) {
		inputs.add("'test'"); expect.add(Arrays.asList("test"));
		inputs.add("more'test'"); expect.add(Arrays.asList("test"));
		inputs.add("'test'more"); expect.add(Arrays.asList("test"));
		inputs.add("\\'no'yes'"); expect.add(Arrays.asList("yes"));
		inputs.add("a 'one' and 'two' and 'three'..."); expect.add(Arrays.asList("one", "two", "three"));
		inputs.add("nothing at all"); expect.add(Arrays.<String>asList());
		inputs.add("''"); expect.add(Arrays.asList(""));
		inputs.add("''test"); expect.add(Arrays.asList(""));
		inputs.add("test''"); expect.add(Arrays.asList(""));
		inputs.add("te''st"); expect.add(Arrays.asList(""));
		inputs.add("'This is not wrong' and 'this isn\\'t either'"); expect.add(Arrays.asList("This is not wrong", "this isn't either"));
		inputs.add("'tw\\'o repl\\'acements' in 't\\'wo stri\\'ngs'."); expect.add(Arrays.asList("tw'o repl'acements", "t'wo stri'ngs"));
		inputs.add("'\\''"); expect.add(Arrays.asList("'"));
		inputs.add("'''"); expect.add(Arrays.asList(""));
		inputs.add("'test1'\n'test2'"); expect.add(Arrays.asList("test1", "test2"));
		inputs.add("''''"); expect.add(Arrays.asList("", "")); // This one is hard. Hint: \G
	}
	
	@Test
	public void testFindSingleQuotedTextWithEscapes() {
		ArrayList<String> inputs = new ArrayList<String>();
		ArrayList<List<String>> expect = new ArrayList<List<String>>();
		
		fillSingleQuotedTestInputs(inputs, expect);

		for (int i = 0; i < inputs.size(); ++i) {
			List<String> output = reg.findSingleQuotedTextWithEscapes(inputs.get(i));
			assertEquals(String.format("Test %d failed: Search <<%s>>", i, inputs.get(i)), expect.get(i), output);
		}
	}
	

	
	@Test
	public void testFindDoubleQuotedTextWithEscapes() {
		ArrayList<String> inputs = new ArrayList<String>();
		ArrayList<List<String>> expect = new ArrayList<List<String>>();
		
		fillSingleQuotedTestInputs(inputs, expect);
		
		for (int i = 0; i < inputs.size(); ++i) {
			String input = inputs.get(i).replaceAll("'", "\""); // Replace single with double quotes.
			List<String> expectSquoteList = expect.get(i);
			ArrayList<String> expectList = new ArrayList<String>(expectSquoteList.size());
			for (String str : expectSquoteList) {
				if (str != null)
					expectList.add(str.replaceAll("'", "\""));
				else
					expectList.add(null);
			}
			
			List<String> output = reg.findDoubleQuotedTextWithEscapes(input);
			assertEquals(String.format("Test %d failed: Search <<%s>>", i, input), expectList, output);
		}
	}


	@Test
	public void testParseDate()  {
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		ArrayList<String> inputs = new ArrayList<String>();
		ArrayList<Long> expect = new ArrayList<Long>();
		
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.set(Calendar.MILLISECOND, 0);
		
		cal.set(2012,4,23,1,23,31);
		long time1 = cal.getTimeInMillis();
		
		inputs.add("Wed, 23-May-12 01:23:31 GMT"); expect.add(time1);	
		inputs.add("23-May-12 01:23:31 GMT"); expect.add(time1);	
		inputs.add("23-May-12 01:23:31"); expect.add(time1);	
		inputs.add("23 May 2012 01:23:31 GMT"); expect.add(time1);	
		inputs.add("23 May  2012   01:23:31     GMT"); expect.add(time1);
		inputs.add("Wed,   23 May  2012   01:23:31     GMT"); expect.add(time1);
		inputs.add("23 mAy 2012 01:23:31 GMT"); expect.add(time1);
		inputs.add("23 maY 12 01:23:31"); expect.add(time1);
		inputs.add("23 jan 12 01:23:31"); cal.set(2012,0,23,1,23,31); expect.add(cal.getTimeInMillis());
		inputs.add("01 AUG 12 15:23:31 GMT"); cal.set(2012,7,1,15,23,31); expect.add(cal.getTimeInMillis());
		inputs.add("01 bla 12 15:23:31 GMT"); expect.add(null);
		inputs.add("1 May 2012 15:23:31 GMT"); expect.add(null);
		inputs.add("01 May 2012 15:23:31 BLA"); expect.add(null);
		inputs.add("01 May 2012-15:23:31"); expect.add(null);
		inputs.add("01 May 2012-15/23:31"); expect.add(null);
		inputs.add("01 May 2012-15/23:31"); expect.add(null);
		
		for (int i = 0; i < inputs.size(); ++i) {
			Long expectTime = expect.get(i);
			Calendar output = reg.parseDate(inputs.get(i));
			if (expectTime == null) {
				assertNull(String.format("Test %d failed: Parsing <<%s>> (should be null)", i, inputs.get(i)), output);
				continue;
			} else {
				assertNotNull(String.format("Test %d failed: Parsing <<%s>> (unexpected null)", i, inputs.get(i)), output);
			}
			output.set(Calendar.MILLISECOND, 0);
			
			long outTime = output.getTimeInMillis();
			
			assertEquals(String.format("Test %d failed: Parsing <<%s>> (was %s not %s)", 
							i, inputs.get(i), df.format(outTime), df.format(expectTime)), 
					(long) expectTime, outTime);
		}
	}
	
	@Test
	public void testTokenize() {
		ArrayList<String> inputs = new ArrayList<String>();
		ArrayList<List<String>> expect = new ArrayList<List<String>>();
		
		inputs.add("this-string 'has only three tokens'"); expect.add(Arrays.asList("this", "string", "has only three tokens"));
		inputs.add("this*string'has only two@tokens'"); expect.add(Arrays.asList("this", "stringhas only two@tokens"));
		inputs.add("one'two''three' '' four 'twenty-one'"); expect.add(Arrays.asList("onetwothree", "", "four", "twenty-one"));

		for (int i = 0; i < inputs.size(); ++i) {
			List<String> output = reg.wordTokenize(inputs.get(i));
			assertEquals(String.format("Test %d failed: Search <<%s>>", i, inputs.get(i)), expect.get(i), output);
		}
	}
	
	/**
	 * This should pass if you solved the first part of the bonus question.
	 */
	@Test
	public void testParseAVPairs() {
		ArrayList<String> inputs = new ArrayList<String>();
		ArrayList<List<AVPair>> expect = new ArrayList<List<AVPair>>();
		
		inputs.add("key=val"); expect.add(Arrays.asList(new AVPair("key","val")));
		inputs.add("key=val; key2=val2"); expect.add(Arrays.asList(new AVPair("key","val"), new AVPair("key2","val2")));
		inputs.add("key ; key2=val2"); expect.add(Arrays.asList(new AVPair("key",null), new AVPair("key2","val2")));
		inputs.add("  key=val ; key2 = val2"); expect.add(Arrays.asList(new AVPair("key","val"), new AVPair("key2","val2")));
		inputs.add("key = \"val1 three\""); expect.add(Arrays.asList(new AVPair("key","val1 three")));
		inputs.add("key= \"\\\"He said what?\\\"\""); expect.add(Arrays.asList(new AVPair("key","\"He said what?\"")));
		inputs.add("key2 =\"val 2\""); expect.add(Arrays.asList(new AVPair("key2","val 2")));
		inputs.add("key =\"val/1\""); expect.add(Arrays.asList(new AVPair("key","val/1")));
		inputs.add("key1;key2=\"val 2\";key3=\"$#\\\"!'%$#@\" ; key4; key5=val_5*"); expect.add(Arrays.asList(
					new AVPair("key1",null),new AVPair("key2","val 2"),new AVPair("key3","$#\"!'%$#@"),
					new AVPair("key4",null),new AVPair("key5","val_5*") ));
	
		
		for (int i = 0; i < inputs.size(); ++i) {
			List<AVPair> output = reg.parseAvPairs(inputs.get(i));
			assertEquals(String.format("Test %d failed: Search <<%s>>", i, inputs.get(i)), expect.get(i), output);
		}
	}

	/**
	 * Test bad inputs -- this should pass if you solved the second part of the bonus question. 
	 */
	@Test
	public void testParseAVPairsWithInputChecking() {
	    testParseAVPairs();

		ArrayList<String> inputs = new ArrayList<String>();
		inputs.add("key=val ; key2 = val2 three");
		inputs.add("key=val ;; key2 = val2 ");
		inputs.add("key=\"val");
		inputs.add("key=\\\"val");
		inputs.add("key= \"yes\"no");
		inputs.add(";key=val"); 
		inputs.add(";key1=val1;key2=val2;key3=val3");
		inputs.add("key/1=val");
		inputs.add("\"key\"=val");
		
		for (int i = 0; i < inputs.size(); ++i) {
			List<AVPair> output = reg.parseAvPairs2(inputs.get(i));
			assertNull(String.format("Test %d failed: Search <<%s>>", i, inputs.get(i)), output);
		}
	}
}
