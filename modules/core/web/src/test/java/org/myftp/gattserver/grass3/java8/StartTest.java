package org.myftp.gattserver.grass3.java8;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Test;

public class StartTest {

	@Test
	public void test() {

		List<String> strings = new ArrayList<String>();
		strings.add("aaaa");
		strings.add("bb");
		strings.add("aaaada");
		strings.add("edfe");

		strings.forEach(x -> {
			System.out.println(x);
		});

		Consumer<String> consumer = x -> {
			System.out.println("The string is: " + x);
		};

		strings.forEach(consumer);

		// strings.forEach(StringPrinter::printString); // nejde
		strings.forEach(StringPrinter::staticPrintString); // jde, eclipse
															// nenapovídá
		// strings.forEach(StringPrinter::staticPrintInteger); // nejde

		StringPrinter printer = new StringPrinter();
		strings.forEach(printer::printString); 
		
		
		strings.removeIf(x -> {
			return x.startsWith("aa");
		});
		strings.forEach(consumer);
		
	}
}
