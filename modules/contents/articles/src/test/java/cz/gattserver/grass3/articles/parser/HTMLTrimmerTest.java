package cz.gattserver.grass3.articles.parser;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.parser.util.HTMLTrimmer;

public class HTMLTrimmerTest {

	@Test
	public void test() {
		String input = "<aaa>Raz</ddv>";
		String output = HTMLTrimmer.trim(input);
		assertEquals(" Raz", output);
	}

	@Test
	public void test2() {
		String input = "<div class=\"articles-basic-h1\">Motivace</div>";
		String output = HTMLTrimmer.trim(input);
		assertEquals(" Motivace", output);
	}

	@Test
	public void test3() {
		String input = "<div><strong>Arr</strong></div>";
		String output = HTMLTrimmer.trim(input);
		assertEquals(" Arr", output);
	}

}
