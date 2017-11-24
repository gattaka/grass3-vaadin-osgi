package cz.gattserver.grass3.articles.services;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import cz.gattserver.grass3.articles.interfaces.ArticleTO;
import cz.gattserver.grass3.modules.ArticlesContentModule;
import cz.gattserver.grass3.test.AbstractDBUnitTest;

@DatabaseSetup(value = "deleteAll.xml", type = DatabaseOperation.DELETE_ALL)
public class ArticleServiceTest extends AbstractDBUnitTest {

	@Autowired
	private ArticleService articleService;

	@Test
	public void testSaveArticle() {
		Set<String> tags = new HashSet<>();
		tags.add("tag1");
		tags.add("tag2");

		long userId = coreMockService.createMockUser(1);
		long nodeId = coreMockService.createMockRootNode(1);
		long articleId = articleService.saveArticle("mockArticleName", "mockArticleText", tags, true, nodeId, userId,
				"mockContextRoot", ArticleProcessMode.FULL, null, null, null);

		ArticleTO articleTO = articleService.getArticleForDetail(articleId);
		assertNotNull(articleTO);
		assertEquals("mockArticleName", articleTO.getContentNode().getName());
		assertEquals(new Long(nodeId), articleTO.getContentNode().getParent().getId());
		assertEquals(new Long(userId), articleTO.getContentNode().getAuthor().getId());
		assertEquals(new Long(articleId), articleTO.getContentNode().getContentID());
		assertEquals(ArticlesContentModule.ID, articleTO.getContentNode().getContentReaderID());
		assertEquals(2, articleTO.getContentNode().getContentTags().size());
		assertTrue(articleTO.getContentNode().isPublicated());
		assertNull(articleTO.getContentNode().getDraftSourceId());
		assertNull(articleTO.getContentNode().getLastModificationDate());
		assertNotNull(articleTO.getContentNode().getCreationDate());
		assertTrue(articleTO.getPluginCSSResources().isEmpty());
		assertTrue(articleTO.getPluginJSResources().isEmpty());
		assertEquals("mockArticleText", articleTO.getText());
		assertEquals("mockArticleText", articleTO.getOutputHTML());
	}

}
