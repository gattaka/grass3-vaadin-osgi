package cz.gattserver.grass3.articles.editor.lexer;

import static org.junit.Assert.*;

import org.junit.Test;

public class LexerTest {

	@Test
	public void testTab() {
		Lexer lexer = new Lexer("abc\tdef");
		assertEquals(Token.TEXT, lexer.nextToken());
		assertEquals("abc", lexer.getText());
		assertEquals(Token.TAB, lexer.nextToken());
		assertEquals(Token.TEXT, lexer.nextToken());
		assertEquals("def", lexer.getText());
	}

}
