package cz.gattserver.grass3.articles.parser;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.parser.util.FinderArray;
import cz.gattserver.grass3.articles.editor.parser.util.PartsFinder;
import cz.gattserver.grass3.articles.editor.parser.util.PartsFinder.Result;

public class HeaderSplitterTest {

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
		int partsLengthSum = prePart.length() + targetPart.length()
				+ postPart.length();
		assertEquals(result.getCheckSum(), partsLengthSum);
	}
}
