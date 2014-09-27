package cz.gattserver.grass3.java8;

public class StringPrinter {

	public void printString(String x) {
		System.out.println("StringPrinter::printString " + x);
	}

	public static void staticPrintString(String x) {
		System.out.println("StringPrinter::staticPrintString " + x);
	}
	
	public static void staticPrintInteger(Integer x) {
		System.out.println("StringPrinter::staticPrintInteger " + x);
	}

}