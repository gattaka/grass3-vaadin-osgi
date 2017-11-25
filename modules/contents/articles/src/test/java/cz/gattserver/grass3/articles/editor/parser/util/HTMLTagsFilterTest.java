package cz.gattserver.grass3.articles.editor.parser.util;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.parser.util.HTMLTagsFilter;

public class HTMLTagsFilterTest {

	@Test
	public void test() {
		String input = "<aaa>Raz</ddv>";
		String output = HTMLTagsFilter.trim(input);
		assertEquals(" Raz", output);
	}

	@Test
	public void test2() {
		String input = "<div class=\"articles-basic-h1\">Motivace</div>";
		String output = HTMLTagsFilter.trim(input);
		assertEquals(" Motivace", output);
	}

	@Test
	public void test3() {
		String input = "<div><strong>Arr</strong></div>";
		String output = HTMLTagsFilter.trim(input);
		assertEquals(" Arr", output);
	}
	
	@Test
	public void test4() {
		String input = "proto <strong>požaduji</strong>";
		String output = HTMLTagsFilter.trim(input);
		assertEquals("proto  požaduji", output);
	}

}
