package files;

import java.io.IOException;
import java.io.RandomAccessFile;

public class RandomAccess {
	
	/**
	 * Treat the file as an array of (unsigned) 8-bit values and sort them 
	 * in-place using a bubble-sort algorithm.
	 * You may not read the whole file into memory! 
	 * @param file
	 */
	public static void sortBytes(RandomAccessFile file) throws IOException {
		try {
			long current = 0, pos = 0;
			int a, b;
			file.seek(current);
			for (int i = 0; i < file.length(); i++) {
				a = file.read();
				for (int j = (int) pos; j < file.length(); j++) {
					//find the minimum byte in the file
					file.seek(pos);
					b = file.read();
					//if a is bigger then b switch them
					if(a > b){
						file.seek(pos);
						file.write(a);
						file.seek(current);
						file.write(b);
						a = b;
					}
					//advance pos
					pos++;
				}
				//advance current
				current++;
				//reset pos to the next byte, without the smallest one we just found
				pos = current+1;
				//avoid seeking in the last iteration to evade an exception
				if(i < file.length()-1) {
					file.seek(current);
				}
			}
		} catch (IOException e) {
			//print err message to system.err
			System.err.println("Error: " + e);
		}
	}
	
	/**
	 * Treat the file as an array of unsigned 24-bit values (stored MSB first) and sort
	 * them in-place using a bubble-sort algorithm. 
	 * You may not read the whole file into memory! 
	 * @param file
	 * @throws IOException
	 */
	public static void sortTriBytes(RandomAccessFile file) throws IOException {
		try {
			long current = 0, pos = 3;
			int a = 0, b = 0, aSum = 0, bSum = 0;
			file.seek(current);

			//outer loop to find the minimum each iteration
			for (int i = 0; i < file.length()/3 - 1; i++) {

				//set a the value of the first 24-bit value
				for (int m = 0; m < 3; m++) {
					a = file.readUnsignedByte();
					aSum += a;
					if(m != 2){
						aSum <<= 8;
					}
				}

				//inner loop to compare the minimum with all the other values, and switch if needed
				for (int j = i ; j < file.length()/3 - 1; j++) {
					//find the minimum byte in the file
					file.seek(pos);

					//set b the value of the first 24-bit value
					for (int k = 0; k < 3; k++) {
						b = file.readUnsignedByte();
						bSum += b;
						if(k != 2){
							bSum <<= 8;
						}
					}

					//if a is bigger then b switch them
					if(aSum > bSum){
						file.seek(pos);
						//write into file byte by byte with bitwise operations
						file.write((aSum & 0xff0000) >> 16);
						file.write((aSum & 0xff00) >> 8);
						file.write((aSum & 0xff));
						file.seek(current);
						file.write((bSum & 0xff0000) >> 16);
						file.write((bSum & 0xff00) >> 8);
						file.write((bSum & 0xff));
						aSum = bSum;
					}
					//advance pos and reset bSum
					pos += 3;
					bSum = 0;
				}
				//advance current and reset aSum
				current += 3;
				aSum = 0;
				//reset pos to the next byte, without the smallest one
				pos = current + 3;
				//avoid seeking in the last iteration to evade an exception
				if(i < file.length()-1) {
					file.seek(current);
				}
			}
		} catch (IOException e) {
			//print err message to system.err
			System.err.println("Error: " + e);
		}
	}
}
