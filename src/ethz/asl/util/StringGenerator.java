package ethz.asl.util;


import java.util.Random;

public class StringGenerator {
	private static final char[] symbols = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
	private static Random random = new Random();
	public static String get(int length) {

		char[] buf = new char[length];
		  
		for (int idx = 0; idx < buf.length; ++idx) {
			buf[idx] = symbols[random.nextInt(symbols.length)];
		}
		return new String(buf);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(get(Constants.TEXTLENGTH));
		System.out.println(get(Constants.TEXTLENGTH).length());
	}
}
