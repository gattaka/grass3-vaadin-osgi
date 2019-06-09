package cz.gattserver.grass3.articles.model.domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class ArticleJSCodeComparatorTest {

	@Test
	public void testTab() {
		assertEquals(0, new ArticleJSCodeComparator().compare(null, null));

		ArticleJSCode a1 = new ArticleJSCode();
		a1.setExecutionOrder(1);
		ArticleJSCode a2 = new ArticleJSCode();
		a2.setExecutionOrder(0);

		assertEquals(1, new ArticleJSCodeComparator().compare(a1, null));
		assertEquals(1, new ArticleJSCodeComparator().compare(a2, null));

		assertEquals(-1, new ArticleJSCodeComparator().compare(null, a1));
		assertEquals(-1, new ArticleJSCodeComparator().compare(null, a2));

		assertEquals(0, new ArticleJSCodeComparator().compare(a1, a1));
		assertEquals(0, new ArticleJSCodeComparator().compare(a2, a2));

		assertEquals(1, new ArticleJSCodeComparator().compare(a1, a2));
		assertEquals(-1, new ArticleJSCodeComparator().compare(a2, a1));
	}

}
