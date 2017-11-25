package cz.gattserver.grass3.articles.services;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import cz.gattserver.grass3.articles.interfaces.ArticleDraftOverviewTO;
import cz.gattserver.grass3.articles.interfaces.ArticleTO;
import cz.gattserver.grass3.modules.ArticlesContentModule;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.services.UserService;
import cz.gattserver.grass3.test.AbstractDBUnitTest;

@DatabaseSetup(value = "deleteAll.xml", type = DatabaseOperation.DELETE_ALL)
public class ArticleServiceTest extends AbstractDBUnitTest {

	@Autowired
	private ArticleService articleService;

	@Autowired
	private UserService userService;

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
		assertFalse(articleTO.getContentNode().isDraft());
		assertNull(articleTO.getContentNode().getDraftSourceId());
		assertNull(articleTO.getContentNode().getLastModificationDate());
		assertNotNull(articleTO.getContentNode().getCreationDate());

		assertTrue(articleTO.getPluginCSSResources().isEmpty());
		assertTrue(articleTO.getPluginJSResources().isEmpty());
		assertEquals("mockArticleText", articleTO.getText());
		assertEquals("mockArticleText", articleTO.getOutputHTML());
		assertEquals("mockArticleText", articleTO.getSearchableOutput());
	}

	@Test
	public void testSaveArticle_existing() {
		Set<String> tags = new HashSet<>();
		tags.add("tag1");
		tags.add("tag2");

		long userId = coreMockService.createMockUser(1);
		long nodeId = coreMockService.createMockRootNode(1);
		long articleId = articleService.saveArticle("mockArticleName", "mockArticleText", tags, true, nodeId, userId,
				"mockContextRoot", ArticleProcessMode.FULL, null, null, null);

		tags.add("tagNew");

		long articleIdModified = articleService.saveArticle("mockArticleNameNew", "mockArticleTextNew", tags, false,
				nodeId, userId, "mockContextRoot", ArticleProcessMode.FULL, articleId, null, null);

		assertEquals(articleId, articleIdModified);

		ArticleTO articleTO = articleService.getArticleForDetail(articleId);
		assertNotNull(articleTO);
		assertEquals("mockArticleNameNew", articleTO.getContentNode().getName());
		assertEquals(new Long(nodeId), articleTO.getContentNode().getParent().getId());
		assertEquals(new Long(userId), articleTO.getContentNode().getAuthor().getId());
		assertEquals(new Long(articleId), articleTO.getContentNode().getContentID());
		assertEquals(ArticlesContentModule.ID, articleTO.getContentNode().getContentReaderID());
		assertEquals(3, articleTO.getContentNode().getContentTags().size());
		assertFalse(articleTO.getContentNode().isPublicated());
		assertFalse(articleTO.getContentNode().isDraft());
		assertNull(articleTO.getContentNode().getDraftSourceId());
		assertNotNull(articleTO.getContentNode().getLastModificationDate());
		assertNotNull(articleTO.getContentNode().getCreationDate());

		assertTrue(articleTO.getPluginCSSResources().isEmpty());
		assertTrue(articleTO.getPluginJSResources().isEmpty());
		assertEquals("mockArticleTextNew", articleTO.getText());
		assertEquals("mockArticleTextNew", articleTO.getOutputHTML());
		assertEquals("mockArticleTextNew", articleTO.getSearchableOutput());
	}

	@Test
	public void testSavePreview() {
		Set<String> tags = new HashSet<>();
		tags.add("tag1");
		tags.add("tag2");

		long userId = coreMockService.createMockUser(1);
		long nodeId = coreMockService.createMockRootNode(1);
		long articleId = articleService.saveArticle("mockArticleName", "mockArticleText", tags, true, nodeId, userId,
				"mockContextRoot", ArticleProcessMode.PREVIEW, null, null, null);

		ArticleTO articleTO = articleService.getArticleForDetail(articleId);
		assertNotNull(articleTO);
		assertEquals("mockArticleName", articleTO.getContentNode().getName());
		assertEquals(new Long(nodeId), articleTO.getContentNode().getParent().getId());
		assertEquals(new Long(userId), articleTO.getContentNode().getAuthor().getId());
		assertEquals(new Long(articleId), articleTO.getContentNode().getContentID());
		assertEquals(ArticlesContentModule.ID, articleTO.getContentNode().getContentReaderID());
		assertEquals(2, articleTO.getContentNode().getContentTags().size());
		assertTrue(articleTO.getContentNode().isPublicated());
		assertTrue(articleTO.getContentNode().isDraft());
		assertNull(articleTO.getContentNode().getDraftSourceId());
		assertNull(articleTO.getContentNode().getLastModificationDate());
		assertNotNull(articleTO.getContentNode().getCreationDate());

		assertTrue(articleTO.getPluginCSSResources().isEmpty());
		assertTrue(articleTO.getPluginJSResources().isEmpty());
		assertEquals("mockArticleText", articleTO.getText());
		assertEquals("mockArticleText", articleTO.getOutputHTML());
		assertEquals("mockArticleText", articleTO.getSearchableOutput());
	}

	@Test
	public void testSaveDraft() {
		Set<String> tags = new HashSet<>();
		tags.add("tag1");
		tags.add("tag2");

		long userId = coreMockService.createMockUser(1);
		long nodeId = coreMockService.createMockRootNode(1);
		long draftId = articleService.saveArticle("mockArticleName", "mockArticleText", tags, true, nodeId, userId,
				"mockContextRoot", ArticleProcessMode.DRAFT, null, null, null);

		ArticleTO articleTO = articleService.getArticleForDetail(draftId);
		assertNotNull(articleTO);
		assertEquals("mockArticleName", articleTO.getContentNode().getName());
		assertEquals(new Long(nodeId), articleTO.getContentNode().getParent().getId());
		assertEquals(new Long(userId), articleTO.getContentNode().getAuthor().getId());
		assertEquals(new Long(draftId), articleTO.getContentNode().getContentID());
		assertEquals(ArticlesContentModule.ID, articleTO.getContentNode().getContentReaderID());
		assertEquals(2, articleTO.getContentNode().getContentTags().size());
		assertTrue(articleTO.getContentNode().isPublicated());
		assertTrue(articleTO.getContentNode().isDraft());
		assertNull(articleTO.getContentNode().getDraftSourceId());
		assertNull(articleTO.getContentNode().getLastModificationDate());
		assertNotNull(articleTO.getContentNode().getCreationDate());

		assertTrue(articleTO.getPluginCSSResources().isEmpty());
		assertTrue(articleTO.getPluginJSResources().isEmpty());
		assertEquals("mockArticleText", articleTO.getText());
		assertNull(articleTO.getOutputHTML());
		assertNull(articleTO.getSearchableOutput());
	}

	@Test
	public void testGetDraftsForUser() {
		Set<String> tags = new HashSet<>();
		tags.add("tag1");
		tags.add("tag2");

		long userId = coreMockService.createMockUser(1);
		long nodeId = coreMockService.createMockRootNode(1);

		long draftId = articleService.saveArticle("mockArticleName", "mockArticleText", tags, true, nodeId, userId,
				"mockContextRoot", ArticleProcessMode.DRAFT, null, null, null);

		articleService.saveArticle("mockArticleName", "mockArticleText", tags, true, nodeId, userId, "mockContextRoot",
				ArticleProcessMode.FULL, null, null, null);

		long userId2 = coreMockService.createMockUser(2);
		long draftId2 = articleService.saveArticle("mockArticleName", "mockArticleText", tags, true, nodeId, userId2,
				"mockContextRoot", ArticleProcessMode.DRAFT, null, null, null);

		List<ArticleDraftOverviewTO> list = articleService.getDraftsForUser(userId);
		assertEquals(1, list.size());
		assertEquals(new Long(draftId), list.get(0).getId());

		list = articleService.getDraftsForUser(userId2);
		assertEquals(1, list.size());
		assertEquals(new Long(draftId2), list.get(0).getId());
	}

	@Test
	public void testGetDraftsForUser_asAdmin() {
		Set<String> tags = new HashSet<>();
		tags.add("tag1");
		tags.add("tag2");

		long userId = coreMockService.createMockUser(1);
		long nodeId = coreMockService.createMockRootNode(1);

		long draftId = articleService.saveArticle("mockArticleName", "mockArticleText", tags, true, nodeId, userId,
				"mockContextRoot", ArticleProcessMode.DRAFT, null, null, null);

		articleService.saveArticle("mockArticleName", "mockArticleText", tags, true, nodeId, userId, "mockContextRoot",
				ArticleProcessMode.FULL, null, null, null);

		long userId2 = coreMockService.createMockUser(2);
		long draftId2 = articleService.saveArticle("mockArticleName", "mockArticleText", tags, true, nodeId, userId2,
				"mockContextRoot", ArticleProcessMode.DRAFT, null, null, null);

		List<ArticleDraftOverviewTO> list = articleService.getDraftsForUser(userId);
		assertEquals(1, list.size());
		assertEquals(new Long(draftId), list.get(0).getId());

		Set<Role> roles = new HashSet<>();
		roles.add(Role.ADMIN);
		userService.changeUserRoles(userId, roles);

		list = articleService.getDraftsForUser(userId);
		assertEquals(2, list.size());
		assertEquals(new Long(draftId2), list.get(0).getId());
		assertEquals(new Long(draftId), list.get(1).getId());
	}
}
