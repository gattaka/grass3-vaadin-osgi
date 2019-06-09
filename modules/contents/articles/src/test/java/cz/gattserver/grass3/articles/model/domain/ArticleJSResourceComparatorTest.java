package cz.gattserver.grass3.articles.model.domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class ArticleJSResourceComparatorTest {

	@Test
	public void testTab() {
		assertEquals(0, new ArticleJSResourceComparator().compare(null, null));

		ArticleJSResource a1 = new ArticleJSResource();
		a1.setExecutionOrder(1);
		ArticleJSResource a2 = new ArticleJSResource();
		a2.setExecutionOrder(0);
		
		assertEquals(1, new ArticleJSResourceComparator().compare(a1, null));
		assertEquals(1, new ArticleJSResourceComparator().compare(a2, null));

		assertEquals(-1, new ArticleJSResourceComparator().compare(null, a1));
		assertEquals(-1, new ArticleJSResourceComparator().compare(null, a2));
		
		assertEquals(0, new ArticleJSResourceComparator().compare(a1, a1));
		assertEquals(0, new ArticleJSResourceComparator().compare(a2, a2));
		
		assertEquals(1, new ArticleJSResourceComparator().compare(a1, a2));
		assertEquals(-1, new ArticleJSResourceComparator().compare(a2, a1));
	}

}
