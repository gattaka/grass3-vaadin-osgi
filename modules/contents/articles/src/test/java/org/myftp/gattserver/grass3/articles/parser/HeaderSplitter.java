package org.myftp.gattserver.grass3.articles.parser;

import java.io.IOException;
import java.io.InputStream;
import static org.junit.Assert.*;

import org.junit.Test;
import org.myftp.gattserver.grass3.articles.parser.PartsFinder.Result;

public class HeaderSplitter {

	@Test
	public void testSearchWindow() {

		FinderArray searchWindow = new FinderArray();

		searchWindow.addChar('a');
		searchWindow.addChar('b');
		searchWindow.addChar('c');
		searchWindow.addChar('d');
		searchWindow.addChar('e');

		assertEquals('b', searchWindow.getChar(0));
		assertEquals('c', searchWindow.getChar(1));
		assertEquals('d', searchWindow.getChar(2));
		assertEquals('e', searchWindow.getChar(3));
	}

	@Test
	public void test() throws IOException {

		InputStream in = this.getClass().getResourceAsStream("TestText.txt");

		Result result = PartsFinder.findParts(in, 2);

		String prePart = result.getPrePart();
		String targetPart = result.getTargetPart();
		String postPart = result.getPostPart();

		System.out.println("prePart " + prePart.length());
		System.out.println("targetPart " + targetPart.length());
		System.out.println("postPart " + postPart.length());
		int partsLengthSum = prePart.length() + targetPart.length()
				+ postPart.length();

		System.out.println("Sum: " + partsLengthSum);
		System.out.println("Builder: " + result.getCheckSum());
		assertEquals(result.getCheckSum(), partsLengthSum);

		System.out
				.println("prePart: -----------------------------------------------");
		System.out.println(prePart);
		System.out
				.println("targetPart: --------------------------------------------");
		System.out.println(targetPart);
		System.out
				.println("postPart: ----------------------------------------------");
		System.out.println(postPart);

	}
}
