import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.Stack;

/**
 * Program class for an SRPN calculator.
 * 
 * @author Jordan Koulouris
 * @version 1.0
 * @release 28/11/2017
 * 
 */
public class SRPN {
	private Stack<String> input = new Stack<String>();

	/**
	 * Method to check int saturation by using BigInteger for comparison
	 * @param value
	 *      The BigInteger value to check for saturation
	 * @return result
	 * 		Return the same value as int, reset to int max or min if saturated
	 * 
	 */
	private int checkSaturation(BigInteger value) {
		int result = value.intValue();
		
		if (value.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) == 1) {
			result = Integer.MAX_VALUE;
		} else if (value.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) == -1) {
			result = Integer.MIN_VALUE;
		}
		return result;
	}

	/**
	 * Method used to evaluate the stack when given an operator,
	 * and push result back to the stack.
	 * @param input
	 *		stack holding the current input arguments, including the operator to be used
	 * 
	 */
	private void evaluateExpression(Stack<String> input) {
		String operator = input.pop();
		int result = 0;

		//Perform required print if given 'd' or '='
		if (operator.equals("d")) {
			printStack(input);

		} else if (operator.equals("=")) {
			if (input.isEmpty()) {
				System.out.println("Stack empty.");
			} else {
				System.out.println(Integer.parseInt(input.peek()));
			}
			
		} else {
			long top = Integer.parseInt(input.pop());
			long second = Integer.parseInt(input.pop());

			try {
				// Perform operation, check for saturation and push result back to stack as string
				if (operator.equals("+")) {
					result = checkSaturation(BigInteger.valueOf(top + second));
					input.push(Integer.toString(result));

				} else if (operator.equals("-")) {
					result = checkSaturation(BigInteger.valueOf(second - top));
					input.push(Integer.toString(result));

				} else if (operator.equals("*")) {
					result = checkSaturation(BigInteger.valueOf(top * second));
					input.push(Long.toString(result));

				} else if (operator.equals("/")) {
					// No saturation
					result = (int) (second / top);
					input.push(Integer.toString(result));

				} else if (operator.equals("%")) {
					// No saturation
					result = (int) (second % top);
					input.push(Integer.toString(result));

				} else if (operator.equals("^")) {
					// Check for negative power
					if (top < 0) {
						System.out.println("Negative power.");
						input.push(Long.toString(second));
						input.push(Long.toString(top));

					} else {
						result = (int) Math.pow(second, top);
						result = checkSaturation(BigInteger.valueOf(result));
						input.push(Integer.toString(result));
					}
				}
				
			// Catch the Arithmetic Exception caused by dividing or modulus by zero.
			} catch (ArithmeticException e) {
				input.push(Long.toString(second));
				input.push(Long.toString(top));
				System.out.println("Divide by 0.");
			}
		}
	}

	/**
	 * Finds the next random number from RandomGenerator.
	 * @return random
	 * 		The next random int from the generator	
	 * 
	 */
	private int getRand() {
		RandomGenerator random = new RandomGenerator();
		return random.nextRandom();
	}
	
	/**
	 * Method to determine if operator has been input.
	 * @param check
	 *		The input string (a single character) to check.
	 * @return 
	 * 		Returns true if the input is an operator
	 * 
	 */
	private boolean isOperator(String check) {
		String[] operatorList = { "+", "-", "*", "/", "%", "^", "=", "d" };

		// Loop through and compare against each allowable operator
		for (String operator : operatorList) {
			if (check.equals(operator)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Method to print out all of the current stack.
	 * @param input
	 *		The stack of input to print
	 * 
	 */
	private void printStack(Stack<String> input) {
		for (String entry : input) {
			System.out.println(entry);
		}
	}

	/**
	 * Method to process each allowable line of input given by the user.
	 * @param line
	 *		The current line of usable input to be processed, in string form
	 * 
	 */
	public void processCommand(String line) {
		//Deal with operator
		if (isOperator(line)) {
			// Print error if not enough arguments have been given for operator, not required for '=' and 'd'
			if (input.size() < 2 && !line.equals("=") && !line.equals("d")) {
				System.out.println("Stack underflow.");
			} else {
				input.push(line);
				evaluateExpression(input);
			}
		}
		//Deal with digit
		else {
			//Print out error if stack has reached max size
			if (input.size() == 23) {
				System.out.println("Stack overflow.");
				
			}
			//Check for Octal number and convert 
			else if (line.length() > 1 && (line.charAt(0) == '0' || (line.charAt(0) == '-' && line.charAt(1) == '0'))) {
				for (int i = 0; i < line.length(); i++) {
					if (line.charAt(i) == '8' || line.charAt(i) == '9') {
						line = line.substring(0,i);
						break;
					}
				}
				BigInteger inputOct = new BigInteger(line, 8);
				int checkedOct = checkSaturation(inputOct);
				input.push(Integer.toString(checkedOct));
			}
			//Else check for saturation and push to stack
			else {
				BigInteger inputInt = new BigInteger(line);
				int checkedInt = checkSaturation(inputInt);
				input.push(Integer.toString(checkedInt));
			}
		}
	}

	/**
	 * Method to filter input given process each command separately.
	 * @param inputLine
	 *		The line of input given.
	 * 
	 */
	private void filterString(String inputLine) {
		StringBuilder digit = new StringBuilder();

		// Loop through and evaluate each character in inputLine
		for (int i = 0; i < inputLine.length(); i++) {
			if (Character.isDigit(inputLine.charAt(i))) {
				digit.append(inputLine.charAt(i));
				
				// Process number if end of string is reached
				if (i == inputLine.length()-1) {
					processCommand(digit.toString());
					digit.setLength(0);
				}
				
			//Prevent negative number being confused with subtraction (digit directly after '-')
			} else if (i != inputLine.length()-1 && inputLine.charAt(i) == '-' && Character.isDigit(inputLine.charAt(i+1))) {
				//Process any previously found number
				if (digit.length() > 0) {
					processCommand(digit.toString());
					digit.setLength(0);
				}
				
				//Add '-' symbol to start building the new minus number
				digit.append(inputLine.charAt(i));
				
			} else {
				//If a number was found and now has ended, process the number.
				if (digit.length() > 0) {
					processCommand(digit.toString());
					digit.setLength(0);
				}

				//If a '#' is found, break the loop.
				if (inputLine.charAt(i) == '#') {
					break;
				}
				//If a 'r' is found, look for 'rachid' or get next random number.
				else if (inputLine.charAt(i) == 'r') {
					if (i < inputLine.length()-5 && inputLine.substring(i, i+6).equals("rachid")) {
						System.out.println("Rachid is the best unit lecturer.");
						i += 5;
					} else {
						int rand = getRand();
						processCommand(Integer.toString(rand));
					}
				}
				//If an operator is found, send it for processing
				else if (isOperator(inputLine.substring(i, i + 1))) {
					processCommand(inputLine.substring(i, i + 1));
				}

				//Otherwise, print error for all other unrecognised input characters (ignore whitespace).
				else if (inputLine.charAt(i) != ' ') {
					System.out.printf("Unrecognised operator or operand \"%c\".\n", inputLine.charAt(i));
				}
			}
		}
	}

	/**
	 * Main method for accepting input from the user.
	 * 
	 */
	public static void main(String[] args) {
		SRPN sprn = new SRPN();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		try {
			// Keep on accepting input from the command-line
			while (true) {
				String command = reader.readLine();

				// Close on an End-of-file (EOF) (Ctrl-D on the terminal)
				if (command == null) {
					// Exit code 0 for a graceful exit
					System.exit(0);
				} else {
					// Otherwise send the input to be filtered and processed.
					sprn.filterString(command);
				}
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
}