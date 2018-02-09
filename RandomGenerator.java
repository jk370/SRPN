import java.util.Random;

/**
 * Random number generator for SRPN. Prints a consistent first 23 values.
 * 
 * @author Jordan Koulouris
 * @version 1.0
 * @release 28/11/2017
 * @see SRPN.java
 *
 */
public class RandomGenerator {
	private int[] randoms;
	private static int location;
	
	/**
	 * Constructor. Initialises the random number array for first 23 values from SRPN output.
	 * 
	 */
	public RandomGenerator() {
		randoms = new int[]{ 1804289383, 846930886, 1681692777, 1714636915,
				1957747793, 424238335, 719885386, 1649760492,
				596516649, 1189641421, 1025202362, 1350490027,
				783368690, 1102520059, 2044897763, 1967513926,
				1365180540, 1540383426, 304089172, 1303455736,
				35005211, 521595368, 294702567 };
	}
	
	/**
	 * Method to return the next random number, either from list or using next random int.
	 * @return random
	 * 		the next random number
	 * 
	 */
	public int nextRandom() {
		int random;
		
		if (location == randoms.length) {
			Random r = new Random();
			random = r.nextInt() & Integer.MAX_VALUE; //Keeps it positive without overflowing
		}
		
		else {
			random = randoms[location];
			location++;
		}
		
		return random;
	}
}