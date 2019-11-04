public class Bits {

	/**
	 * Given an 8-byte long composed of the bytes B_1, B_2, ... , B_8, return the long with byte order reversed:
	 * B_8, B_7, ..., B_1
	 * The implementation of this method shouldn't use any function calls.
	 * @param a the number to reverse
	 * @return the reverse of a
	 */
	public static long byteReverse(long a) {
		long b = 0xff00000000000000L;	//the variable that removes 1's from a
		long c = 0x0000000000000000L;					//takes the 1's from b and moves them to result
		long res = 0x0000000000000000L;				//the final result
		for (int i = 0; i < 8; i++) {
			if ( i<4) {
				c = ((a & b) >>> (56 - (16 * i)));                    //sets c each loop to bitwise and between a and b
			} else {
				c = ((a & b) << (8 + (16 * (i-4))));                    //sets c each loop to bitwise and between a and b
			}
			b = b>>>8;													//unsign shift 8 bits to the right, pushing 0 in
			res = res | c;												//res gets the old res and the new c
		}
		return res;
	}
	
	/**
	 * Given a 32-bit integer composed of 32 bits: b_31,b_30,...b_1,b_0,  return the integer whose bit representation is
	 * b_{31-n},b_{30-n},...,b_1,b_0,b_31,...,b_{31-n+1}. 
	 * The implementation of this method shouldn't use any control structures (if-then, loops) or function calls.
	 * @param a the integer that we are rotating left (ROLing)
	 * @param n the number of bits to rotate.
	 * @return the ROL of a
	 */
	public static int rol(int a, int n) {
		int b = a;				//set b to be the same as a
		a = a<<n;				//set the 32-n last bits
		b = b >>>(32-n);		//set the last n bits
		return a | b;			//return their bitwise
	}

	/**
	 * Given two 32-bit integers a_31,...,a_0 and b_31,...,b_0, return the 64-bit long that contains their bits interleaved:
	 * a_31,b_31,a_30,b_30,...,a_0,b_0.
	 * The implementation of this method shouldn't use any function calls.
	 * @param a
	 * @param b
	 * @return their interleaved long
	 */
	public static long interleave(int a, int b) {
		long ab = 0;
		int c = 0x80000000;
		long tempA = 0x0000000000000000L;
		long tempB = 0x0000000000000000L;

		for (int i = 0; i < 32; i++) {
			//tack the relevant bit from a and b integers a and b
			tempA = c&a;
			tempB = c&b;

			// push the bit to the correct location
			tempA = tempA << (32-i);
			tempB = tempB << (32-i);
			tempB = tempB >>> 1;		//now move tempB one to the right, i made this because when doing & with an int all left most bits will be 1

			//enter the bits to the final long ab
			ab = ab | tempA;
			ab = ab | tempB;
			c = c>>>1;				//shift c one bit to the right
		}
		return ab;
	}
	
	/**
	 * Pack several values into a compressed 32-bit representation. 
	 * The packed representation should contain
	 * <table>
	 * <tr><th>bits</th>	<th>value</th></tr>
	 * <tr><td>31</td>		<td>1 if b1 is true, 0 otherwise</td></tr>
	 * <tr><td>30-23</td>	<td>the value of the byte a</td></tr>
	 * <tr><td>22</td>		<td>1 if b2 is true, 0 otherwise</td></tr>
	 * <tr><td>21-6</td>	<td>the value of the char c</td></tr>
	 * <tr><td>5-0</td>		<td>the constant binary value 101101</td></tr>
	 * </table>
	 * The implementation of this method shouldn't use any control structures (if-then, loops) or function calls
	 * (you may use the conditional operator "?:").
	 * @param a
	 * @param b1
	 * @param b2
	 * @param c
	 * @return
	 */
	public static int packStruct(byte a, boolean b1, boolean b2, char c) {
		int pack = 0;
		pack = (b1)? 1:0;		//setting b1
		pack <<= 8;				//shifting to the right position
		pack |= a & 0xff;		//setting the byte
		pack <<= 1;
		pack |= (b2)? 1:0;		//follow same pattern for all parameters
		pack <<= 16;
		pack |= c;
		pack <<= 6;
		pack |= 0b101101;
		return pack;
	}
	
	/**
	 * Given a packed struct (with the same format as {@link #packStruct(byte, boolean, boolean, char)}, update
	 * its byte value (bits 23-30) to the new value a.
	 * The implementation of this method shouldn't use any control structures (if-then, loops) or function calls.
	 * @param struct
	 * @param a
	 * @return
	 */
	public static int updateStruct(int struct, byte a) {
		struct &= 0b10000000011111111111111111111111;
		int b = a & 0xff;
		b <<= 23;
		struct |= b;
		return struct;
	}
}
