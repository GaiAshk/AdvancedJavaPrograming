import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import static il.ac.idc.ap.solutions.datatypes.Bases.*;

/**
 * This class only tests simple examples (to give a sanity-check).
 * Careful: Correct code will always pass the tests, but not all code that
 * passes these tests is correct.
 * @author talm
 *
 */
public class BasesTest {
	public final static int NUM_EXTENDED = 32; // number of extended tests
	public final static int MAX_DIGITS= 32; // maximum number of digits
	public final static int MAX_BASE = 64; // maximum base to test
	
	Random rnd;
	
	@Before
	public void setup() {
		rnd = new Random(2); // Fixed seed so tests will be repeatable.
	}

	@Test
	public void testConvertFromBase() {
		int digits[] = {6, 10, 14, 13, 11, 8};
		assertEquals(12345678L, Bases.convertFromBase(digits, 17));
	}
	

	@Test
	public void testConvertFromBaseExtended() {
		for (int i = 0; i < NUM_EXTENDED; ++i) {
			int numDigits = rnd.nextInt(MAX_DIGITS) + 1;
			int base = rnd.nextInt(MAX_BASE - 2) + 2;
			long num = rnd.nextLong() >>> 1;
			int digits[] = new int[numDigits];
			convertToBase(num, base, digits);
			num = convertFromBase(digits, base); // digits might be too short to hold original num.
			
			assertEquals(String.format("Error in test %d", i),
					num, Bases.convertFromBase(digits, base));
		}
	}

	@Test
	public void testConvertToBase() {
		int expectedDigits[] = {6, 10, 14, 13, 11};
		int testDigits[] = new int[5];
		
		Bases.convertToBase(12345678L, 17, testDigits);
		assertArrayEquals("Incorrect base conversion", expectedDigits, testDigits);
	}
	

	@Test
	public void testConvertToBaseExtended() {
		for (int i = 0; i < NUM_EXTENDED; ++i) {
			int numDigits = rnd.nextInt(MAX_DIGITS) + 1;
			int base = rnd.nextInt(MAX_BASE - 2) + 2;
			long num = rnd.nextLong() >>> 1; // Make sure it's not negative
			int digitsExpected[] = new int[numDigits];
			int digits[] = new int[numDigits];
			
			convertToBase(num, base, digitsExpected);
			Bases.convertToBase(num, base, digits);
			
			assertArrayEquals(String.format("Error in test %d", i),
					digitsExpected, digits);
		}
	}
	
	@Test
	public void testBaseAdd() {
		int aDigits[] = 	   {6, 10, 14, 13, 11, 8};
		int bDigits[] = 	   {15, 0, 10, 16, 13, 1};
		int expectedDigits[] = {4, 11, 7 , 13, 8};
		int testDigits[] = new int[5];
		
		Bases.baseAdd(17, aDigits, bDigits, testDigits);
		assertArrayEquals("Incorrect base addition", expectedDigits, testDigits);
	}
	

	@Test
	public void testBaseAddExtended() {
		for (int i = 0; i < NUM_EXTENDED; ++i) {
			int numDigits = rnd.nextInt(MAX_DIGITS) + 1;
			int base = rnd.nextInt(MAX_BASE - 2) + 2;
			int aDigits[] = new int[numDigits];
			int bDigits[] = new int[numDigits];
			
			
			int expectedOut[];
			if (numDigits > 1 && rnd.nextBoolean()) {
				expectedOut = new int[rnd.nextInt(numDigits - 1) + 1];
			} else {
				expectedOut = new int[numDigits];
			}
				
			int testOut[] = new int[expectedOut.length];
			
			for (int j = 0; j < numDigits; ++j) {
				aDigits[j] = rnd.nextInt(base);
				bDigits[j] = rnd.nextInt(base);
			}
			
			baseAdd(base, aDigits, bDigits, expectedOut);
			Bases.baseAdd(base, aDigits, bDigits, testOut);
			
			assertArrayEquals(String.format("Error in test %d", i),
					expectedOut, testOut);
		}
	}
	
	@Test
	public void testBaseNegate() {
		int inDigits[] = 	   {1, 0, 0, 0, 0, 0};
		int expectedDigits[] = {16, 16, 16, 16, 16};
		int testDigits[] = new int[5];
		
		Bases.baseNegate(17, inDigits, testDigits);
		assertArrayEquals("Incorrect base negation", expectedDigits, testDigits);
	}
	

	@Test
	public void testBaseNegateExtended() {

		for (int i = 0; i < NUM_EXTENDED; ++i) {
			int numDigits = rnd.nextInt(MAX_DIGITS) + 1;
			int base = rnd.nextInt(MAX_BASE - 2) + 2;
			int inDigits[] = new int[numDigits];
			int expectedDigits[] = new int[numDigits];
			int outDigits[] = new int[numDigits];

			for (int j = 0; j < numDigits; ++j) {
				inDigits[j] = rnd.nextInt(base);
			}
			
			baseNegate(base, inDigits, expectedDigits);
			Bases.baseNegate(base, inDigits, outDigits);
			
			assertArrayEquals(String.format("Error in test %d", i),
					expectedDigits, outDigits);
		}
	}
}
