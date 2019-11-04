import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import static il.ac.idc.ap.solutions.datatypes.Bits.*;


/**
 * This class only tests simple examples (to give a sanity-check).
 * Careful: Correct code will always pass the tests, but not all code that
 * passes these tests is correct.
 * @author talm
 *
 */
public class BitsTest {

	public final static int NUM_EXTENDED = 32;
	
	Random rnd;
	
	@Before
	public void setup() {
		rnd = new Random(1); // Fixed seed so tests will be repeatable.
	}
	
	@Test
	public void testReverse() {
		assertEquals(0x0807060504030201L, Bits.byteReverse(0x0102030405060708L));
	}
	
	@Test
	public void testReverseExtended() {
		for (int i = 0; i < NUM_EXTENDED; ++i) {
			long testVector = rnd.nextLong();
			assertEquals(String.format("Error in vector %d", i),
					byteReverse(testVector), Bits.byteReverse(testVector));
		}
	}
	
	@Test
	public void testRol() {
		assertEquals(0xe1eaaf31, Bits.rol(0x8f0f5579, 5));
	}
	
	@Test
	public void testRolExtended() {
		for (int i = 0; i < NUM_EXTENDED; ++i) {
			int testVector = rnd.nextInt();
			assertEquals(String.format("Error in vector %d: 0x%08x,%d", i, testVector, i % 32),
					rol(testVector, i % 32), Bits.rol(testVector, i % 32));
		}
	}
	
	@Test
	public void testInterleave() {
		assertEquals(0xaaaaaaaaaaaaaaaaL, Bits.interleave(0xffffffff, 0));
	}

	@Test
	public void testInterleaveExtended() {
		for (int i = 0; i < NUM_EXTENDED; ++i) {
			int testVector1 = rnd.nextInt();
			int testVector2 = rnd.nextInt();
			assertEquals(String.format("Error in test %d", i),
					interleave(testVector1, testVector2), Bits.interleave(testVector1, testVector2));
		}
	}
	
	@Test
	public void testPackStruct() {
		assertEquals(0x4ac080ad, Bits.packStruct((byte) 0x95, false, true, (char) 0x0202));
	}
	
	@Test
	public void testPackStructExtended() {
		for (int i = 0; i < NUM_EXTENDED; ++i) {
			byte testByte = (byte) rnd.nextInt();
			boolean testBool1 = rnd.nextBoolean();
			boolean testBool2 = rnd.nextBoolean();
			char testChar = (char) rnd.nextInt();
			assertEquals(String.format("Error in test %d", i),
					packStruct(testByte, testBool1, testBool2, testChar),
					Bits.packStruct(testByte, testBool1, testBool2, testChar));
		}
	}
	
	@Test
	public void testUpdateStruct() {
		assertEquals(0x514080ad, Bits.updateStruct(0x4ac080ad, (byte) 0xa2));
	}
	
	@Test
	public void testUpdateStructExtended() {
		for (int i = 0; i < NUM_EXTENDED; ++i) {
			int testBase = rnd.nextInt();
			byte testByte = (byte) rnd.nextInt();
			assertEquals(String.format("Error in test %d", i),
					updateStruct(testBase, testByte),
					Bits.updateStruct(testBase, testByte));
		}
	}
}
