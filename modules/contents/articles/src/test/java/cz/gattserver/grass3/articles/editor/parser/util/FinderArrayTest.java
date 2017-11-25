package cz.gattserver.grass3.articles.editor.parser.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class FinderArrayTest {

	@Test
	public void test() {
		FinderArray array = new FinderArray();
		array.addChar('a');
		array.addChar('h');
		array.addChar('o');
		array.addChar('j');

		assertEquals('a', array.getChar(0));
		assertEquals('h', array.getChar(1));
		assertEquals('o', array.getChar(2));
		assertEquals('j', array.getChar(3));

		array.addChar('!');

		assertEquals('h', array.getChar(0));
		assertEquals('o', array.getChar(1));
		assertEquals('j', array.getChar(2));
		assertEquals('!', array.getChar(3));
	}

	@Test
	public void testCustomSize() {
		FinderArray array = new FinderArray(5);
		array.addChar('a');
		array.addChar('h');
		array.addChar('o');
		array.addChar('j');

		assertEquals('a', array.getChar(0));
		assertEquals('h', array.getChar(1));
		assertEquals('o', array.getChar(2));
		assertEquals('j', array.getChar(3));
		// https://stackoverflow.com/questions/3893663/can-we-assume-default-array-values-in-java-for-example-assume-that-an-int-arra
		assertEquals('\u0000', array.getChar(4));

		array.addChar('!');

		assertEquals('a', array.getChar(0));
		assertEquals('h', array.getChar(1));
		assertEquals('o', array.getChar(2));
		assertEquals('j', array.getChar(3));
		assertEquals('!', array.getChar(4));

		array.addChar('?');

		assertEquals('h', array.getChar(0));
		assertEquals('o', array.getChar(1));
		assertEquals('j', array.getChar(2));
		assertEquals('!', array.getChar(3));
		assertEquals('?', array.getChar(4));
	}

}
