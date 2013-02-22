package util;

import static org.junit.Assert.*;

import org.junit.Test;

import sandbox.util.URLPathAnalyzer;

public class URLPathAnalyzerTest {

	@Test
	public void testEmpty() {
		String path = "/";
		URLPathAnalyzer analyzer = new URLPathAnalyzer(path);
		assertEquals(true, analyzer.isEmpty());
		assertEquals(null, analyzer.getPathToken(0));
		assertEquals(false, analyzer.startsWith("aaa"));
	}
	
	@Test
	public void testEmpty2() {
		String path = "////";
		URLPathAnalyzer analyzer = new URLPathAnalyzer(path);
		assertEquals(true, analyzer.isEmpty());
		assertEquals(null, analyzer.getPathToken(0));
		assertEquals(false, analyzer.startsWith("aaa"));
	}

	@Test
	public void testEmpty3() {
		String path = "";
		URLPathAnalyzer analyzer = new URLPathAnalyzer(path);
		assertEquals(true, analyzer.isEmpty());
		assertEquals(null, analyzer.getPathToken(0));
		assertEquals(false, analyzer.startsWith("aaa"));
	}
	
	@Test
	public void test() {
		String path = "aaa";
		URLPathAnalyzer analyzer = new URLPathAnalyzer(path);
		assertEquals("aaa", analyzer.getPathToken(0));
		assertEquals(null, analyzer.getPathToken(1));
		assertEquals(true, analyzer.startsWith("aaa"));
	}
	
	@Test
	public void test2() {
		String path = "/aaa";
		URLPathAnalyzer analyzer = new URLPathAnalyzer(path);
		assertEquals("aaa", analyzer.getPathToken(0));
		assertEquals(null, analyzer.getPathToken(1));
		assertEquals(true, analyzer.startsWith("aaa"));
	}
	
	@Test
	public void test3() {
		String path = "///aaa";
		URLPathAnalyzer analyzer = new URLPathAnalyzer(path);
		assertEquals("aaa", analyzer.getPathToken(0));
		assertEquals(null, analyzer.getPathToken(1));
		assertEquals(true, analyzer.startsWith("aaa"));
	}

	@Test
	public void test4() {
		String path = "/aaa/";
		URLPathAnalyzer analyzer = new URLPathAnalyzer(path);
		assertEquals("aaa", analyzer.getPathToken(0));
		assertEquals(null, analyzer.getPathToken(1));
		assertEquals(true, analyzer.startsWith("aaa"));
	}
	
	@Test
	public void test5() {
		String path = "aaa/bbb";
		URLPathAnalyzer analyzer = new URLPathAnalyzer(path);
		assertEquals("aaa", analyzer.getPathToken(0));
		assertEquals("bbb", analyzer.getPathToken(1));
		assertEquals(true, analyzer.startsWith("aaa"));
	}
	
	@Test
	public void test6() {
		String path = "aaa///bbb";
		URLPathAnalyzer analyzer = new URLPathAnalyzer(path);
		assertEquals("aaa", analyzer.getPathToken(0));
		assertEquals("bbb", analyzer.getPathToken(1));
		assertEquals(true, analyzer.startsWith("aaa"));
	}
}
