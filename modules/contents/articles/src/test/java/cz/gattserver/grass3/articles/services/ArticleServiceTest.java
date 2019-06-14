package cz.gattserver.grass3.articles.services;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import cz.gattserver.grass3.articles.events.impl.ArticlesProcessResultEvent;
import cz.gattserver.grass3.articles.interfaces.ArticleDraftOverviewTO;
import cz.gattserver.grass3.articles.interfaces.ArticlePayloadTO;
import cz.gattserver.grass3.articles.interfaces.ArticleTO;
import cz.gattserver.grass3.events.EventBus;
import cz.gattserver.grass3.interfaces.ContentTagOverviewTO;
import cz.gattserver.grass3.modules.ArticlesContentModule;
import cz.gattserver.grass3.security.CoreRole;
import cz.gattserver.grass3.services.UserService;
import cz.gattserver.grass3.test.AbstractDBUnitTest;

@DatabaseSetup(value = "deleteAll.xml", type = DatabaseOperation.DELETE_ALL)
public class ArticleServiceTest extends AbstractDBUnitTest {

	@Autowired
	private EventBus eventBus;

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
		ArticlePayloadTO payload = new ArticlePayloadTO("mockArticleName",
				"[N1]Úvod[/N1][TAG]<strong>mockArticleText</strong>[/TAG] [N2]Header2[/N2] dd [N3]Header3[/N3] ssaas [N4]Header4[/N4] fdd [N1]Konec[/N1] ende",
				tags, true, "mockContextRoot");
		long articleId = articleService.saveArticle(payload, nodeId, userId);

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

		assertFalse(articleTO.getPluginCSSResources().isEmpty());
		assertTrue(articleTO.getPluginJSResources().isEmpty());
		assertTrue(articleTO.getPluginJSCodes().isEmpty());
		assertEquals(
				"[N1]Úvod[/N1][TAG]<strong>mockArticleText</strong>[/TAG] [N2]Header2[/N2] dd [N3]Header3[/N3] ssaas [N4]Header4[/N4] fdd [N1]Konec[/N1] ende",
				articleTO.getText());
		assertEquals(
				"<div class=\"articles-h1\">Úvod <a class=\"articles-h-id\" href=\"0\"></a></div><div class=\"level1\">[TAG]&lt;strong&gt;mockArticleText&lt;/strong&gt;[/TAG] "
						+ "</div><div class=\"articles-h2\">Header2 <a class=\"articles-h-id\" href=\"1\"></a></div><div class=\"level2\"> dd </div><div class=\"articles-h3\">Header3 "
						+ "<a class=\"articles-h-id\" href=\"2\"></a></div><div class=\"level3\"> ssaas </div><div class=\"articles-h4\">Header4 <a class=\"articles-h-id\" href=\"3\">"
						+ "</a></div><div class=\"level4\"> fdd </div><div class=\"articles-h1\">Konec <a class=\"articles-h-id\" href=\"4\"></a></div><div class=\"level1\"> ende</div>",
				articleTO.getOutputHTML());
		assertNull(articleTO.getSearchableOutput());
	}

	@Test
	public void testDeleteArticle() {
		Set<String> tags = new HashSet<>();
		tags.add("tag1");
		tags.add("tag2");

		long userId = coreMockService.createMockUser(1);
		long nodeId = coreMockService.createMockRootNode(1);
		ArticlePayloadTO payload = new ArticlePayloadTO("mockArticleName",
				"[N1]Úvod[/N1][TAG]<strong>mockArticleText</strong>[/TAG] [N2]Header2[/N2] dd [N3]Header3[/N3] ssaas [N4]Header4[/N4] fdd [N1]Konec[/N1] ende",
				tags, true, "mockContextRoot");
		long articleId = articleService.saveArticle(payload, nodeId, userId);

		ArticleTO articleTO = articleService.getArticleForDetail(articleId);
		assertNotNull(articleTO);

		articleService.deleteArticle(articleId);

		articleTO = articleService.getArticleForDetail(articleId);
		assertNull(articleTO);
	}

	@Test
	public void testReprocessArticles() throws InterruptedException, ExecutionException {
		Set<String> tags = new HashSet<>();
		tags.add("tag1");
		tags.add("tag2");

		long userId = coreMockService.createMockUser(1);
		long nodeId = coreMockService.createMockRootNode(1);
		ArticlePayloadTO payload = new ArticlePayloadTO("mockArticleName",
				"[N1]Úvod[/N1][TAG]<strong>mockArticleText</strong>[/TAG] [N2]Header2[/N2] dd [N3]Header3[/N3] ssaas [N4]Header4[/N4] fdd [N1]Konec[/N1] ende",
				tags, true, "mockContextRoot");
		long articleId = articleService.saveArticle(payload, nodeId, userId);

		UUID operationId = UUID.randomUUID();

		ArticlesEventsHandler eventsHandler = new ArticlesEventsHandler();
		eventBus.subscribe(eventsHandler);
		CompletableFuture<ArticlesEventsHandler> future = eventsHandler.expectEvent(operationId);

		articleService.reprocessAllArticles(operationId, "mockContextRoot");

		ArticlesEventsHandler mock = future.get();
		ArticlesProcessResultEvent event = mock.getResultAndDelete(operationId);

		assertTrue(event.isSuccess());

		eventBus.unsubscribe(eventsHandler);

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
		assertNotNull(articleTO.getContentNode().getLastModificationDate());
		assertNotNull(articleTO.getContentNode().getCreationDate());

		assertFalse(articleTO.getPluginCSSResources().isEmpty());
		assertTrue(articleTO.getPluginJSResources().isEmpty());
		assertTrue(articleTO.getPluginJSCodes().isEmpty());
		assertEquals(
				"[N1]Úvod[/N1][TAG]<strong>mockArticleText</strong>[/TAG] [N2]Header2[/N2] dd [N3]Header3[/N3] ssaas [N4]Header4[/N4] fdd [N1]Konec[/N1] ende",
				articleTO.getText());
		assertEquals(
				"<div class=\"articles-h1\">Úvod <a class=\"articles-h-id\" href=\"0\"></a></div><div class=\"level1\">[TAG]&lt;strong&gt;mockArticleText&lt;/strong&gt;[/TAG] "
						+ "</div><div class=\"articles-h2\">Header2 <a class=\"articles-h-id\" href=\"1\"></a></div><div class=\"level2\"> dd </div><div class=\"articles-h3\">Header3 "
						+ "<a class=\"articles-h-id\" href=\"2\"></a></div><div class=\"level3\"> ssaas </div><div class=\"articles-h4\">Header4 <a class=\"articles-h-id\" href=\"3\">"
						+ "</a></div><div class=\"level4\"> fdd </div><div class=\"articles-h1\">Konec <a class=\"articles-h-id\" href=\"4\"></a></div><div class=\"level1\"> ende</div>",
				articleTO.getOutputHTML());
		assertNull(articleTO.getSearchableOutput());
	}

	@Test
	public void testSaveArticleWithPlugin() {
		Set<String> tags = new HashSet<>();
		tags.add("tag1");
		tags.add("tag2");

		long userId = coreMockService.createMockUser(1);
		long nodeId = coreMockService.createMockRootNode(1);
		ArticlePayloadTO payload = new ArticlePayloadTO("mockArticleName",
				"[N1]Úvod[/N1][TAG]<strong>mockArticleText</strong>[/TAG] [MOCK_TAG]5[/MOCK_TAG] [N1]Konec[/N1] ende",
				tags, true, "mockContextRoot");
		long articleId = articleService.saveArticle(payload, nodeId, userId);

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

		assertFalse(articleTO.getPluginCSSResources().isEmpty());
		assertTrue(articleTO.getPluginJSResources().isEmpty());
		assertTrue(articleTO.getPluginJSCodes().isEmpty());
		assertEquals(
				"[N1]Úvod[/N1][TAG]<strong>mockArticleText</strong>[/TAG] [MOCK_TAG]5[/MOCK_TAG] [N1]Konec[/N1] ende",
				articleTO.getText());
		assertEquals(
				"<div class=\"articles-h1\">Úvod <a class=\"articles-h-id\" href=\"0\"></a></div><div class=\"level1\">"
						+ "[TAG]&lt;strong&gt;mockArticleText&lt;/strong&gt;[/TAG] <span>*****</span> </div><div class=\"articles-h1\">Konec "
						+ "<a class=\"articles-h-id\" href=\"1\"></a></div><div class=\"level1\"> ende</div>",
				articleTO.getOutputHTML());
		assertNull(articleTO.getSearchableOutput());
	}

	@Test
	public void testSaveArticleWithJSPlugin() {
		Set<String> tags = new HashSet<>();
		tags.add("tag1");
		tags.add("tag2");

		long userId = coreMockService.createMockUser(1);
		long nodeId = coreMockService.createMockRootNode(1);
		ArticlePayloadTO payload = new ArticlePayloadTO("mockArticleName",
				"[N1]Úvod[/N1][TAG]<strong>mockArticleText</strong>[/TAG] [MOCKJS_TAG]Test-content[/MOCKJS_TAG] [N1]Konec[/N1] ende",
				tags, true, "mockContextRoot");
		long articleId = articleService.saveArticle(payload, nodeId, userId);

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

		assertFalse(articleTO.getPluginCSSResources().isEmpty());
		assertFalse(articleTO.getPluginJSResources().isEmpty());
		assertFalse(articleTO.getPluginJSCodes().isEmpty());

		assertEquals(1, articleTO.getPluginJSResources().size());
		assertEquals("pre-resource", articleTO.getPluginJSResources().iterator().next());

		assertEquals(1, articleTO.getPluginJSCodes().size());
		assertEquals("Test-content", articleTO.getPluginJSCodes().iterator().next());

		assertEquals(
				"[N1]Úvod[/N1][TAG]<strong>mockArticleText</strong>[/TAG] [MOCKJS_TAG]Test-content[/MOCKJS_TAG] [N1]Konec[/N1] ende",
				articleTO.getText());
		assertEquals(
				"<div class=\"articles-h1\">Úvod <a class=\"articles-h-id\" href=\"0\"></a></div><div class=\"level1\">"
						+ "[TAG]&lt;strong&gt;mockArticleText&lt;/strong&gt;[/TAG] JS-loaded </div><div class=\"articles-h1\">Konec "
						+ "<a class=\"articles-h-id\" href=\"1\"></a></div><div class=\"level1\"> ende</div>",
				articleTO.getOutputHTML());
		assertNull(articleTO.getSearchableOutput());
	}

	@Test
	public void testSaveArticle_empty() {
		Set<String> tags = new HashSet<>();
		tags.add("tag1");
		tags.add("tag2");

		long userId = coreMockService.createMockUser(1);
		long nodeId = coreMockService.createMockRootNode(1);
		ArticlePayloadTO payload = new ArticlePayloadTO("mockArticleName", "", tags, true, "mockContextRoot");
		long articleId = articleService.saveArticle(payload, nodeId, userId);

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
		assertTrue(articleTO.getPluginJSCodes().isEmpty());
		assertEquals("", articleTO.getText());
		assertEquals("~ empty ~", articleTO.getOutputHTML());
		assertNull(articleTO.getSearchableOutput());
	}

	@Test
	public void testModifyArticle() {
		Set<String> tags = new HashSet<>();
		tags.add("tag1");
		tags.add("tag2");

		long userId = coreMockService.createMockUser(1);
		long nodeId = coreMockService.createMockRootNode(1);
		ArticlePayloadTO payload = new ArticlePayloadTO("mockArticleName",
				"[N1]Úvod[/N1][TAG]<strong>mockArticleText</strong>[/TAG] [N1]Konec[/N1] ende", tags, true,
				"mockContextRoot");
		long articleId = articleService.saveArticle(payload, nodeId, userId);

		tags.add("tagNew");

		payload = new ArticlePayloadTO("mockArticleNameNew",
				"[N1]Úvod[/N1][TAG]<strong>mockArticleTextNew</strong>[/TAG] [N1]Konec[/N1] ende", tags, false,
				"mockContextRoot");
		articleService.modifyArticle(articleId, payload, null);

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

		assertFalse(articleTO.getPluginCSSResources().isEmpty());
		assertTrue(articleTO.getPluginJSResources().isEmpty());
		assertTrue(articleTO.getPluginJSCodes().isEmpty());
		assertEquals("[N1]Úvod[/N1][TAG]<strong>mockArticleTextNew</strong>[/TAG] [N1]Konec[/N1] ende",
				articleTO.getText());
		assertEquals(
				"<div class=\"articles-h1\">Úvod <a class=\"articles-h-id\" href=\"0\"></a></div><div class=\"level1\">"
						+ "[TAG]&lt;strong&gt;mockArticleTextNew&lt;/strong&gt;[/TAG] </div><div class=\"articles-h1\">Konec "
						+ "<a class=\"articles-h-id\" href=\"1\"></a></div><div class=\"level1\"> ende</div>",
				articleTO.getOutputHTML());
		assertNull(articleTO.getSearchableOutput());
	}

	@Test
	public void testSavePreview() {
		Set<String> tags = new HashSet<>();
		tags.add("tag1");
		tags.add("tag2");

		long userId = coreMockService.createMockUser(1);
		long nodeId = coreMockService.createMockRootNode(1);
		ArticlePayloadTO payload = new ArticlePayloadTO("mockArticleName",
				"[N1]Úvod[/N1][TAG]<strong>mockArticleText</strong>[/TAG] [N1]Konec[/N1] ende", tags, true,
				"mockContextRoot");
		long previewId = articleService.saveDraft(payload, nodeId, userId, true);

		ArticleTO articleTO = articleService.getArticleForDetail(previewId);
		assertNotNull(articleTO);
		assertEquals("mockArticleName", articleTO.getContentNode().getName());
		assertEquals(new Long(nodeId), articleTO.getContentNode().getParent().getId());
		assertEquals(new Long(userId), articleTO.getContentNode().getAuthor().getId());
		assertEquals(new Long(previewId), articleTO.getContentNode().getContentID());
		assertEquals(ArticlesContentModule.ID, articleTO.getContentNode().getContentReaderID());
		assertEquals(2, articleTO.getContentNode().getContentTags().size());
		assertTrue(articleTO.getContentNode().isPublicated());
		assertTrue(articleTO.getContentNode().isDraft());
		assertNull(articleTO.getContentNode().getDraftSourceId());
		assertNull(articleTO.getContentNode().getLastModificationDate());
		assertNotNull(articleTO.getContentNode().getCreationDate());

		assertFalse(articleTO.getPluginCSSResources().isEmpty());
		assertTrue(articleTO.getPluginJSResources().isEmpty());
		assertTrue(articleTO.getPluginJSCodes().isEmpty());
		assertEquals("[N1]Úvod[/N1][TAG]<strong>mockArticleText</strong>[/TAG] [N1]Konec[/N1] ende",
				articleTO.getText());
		assertEquals(
				"<div class=\"articles-h1\">Úvod <a class=\"articles-h-id\" href=\"0\"></a></div><div class=\"level1\">"
						+ "[TAG]&lt;strong&gt;mockArticleText&lt;/strong&gt;[/TAG] </div><div class=\"articles-h1\">Konec "
						+ "<a class=\"articles-h-id\" href=\"1\"></a></div><div class=\"level1\"> ende</div>",
				articleTO.getOutputHTML());
		assertNull(articleTO.getSearchableOutput());
	}

	@Test
	public void testSaveDraft() {
		Set<String> tags = new HashSet<>();
		tags.add("tag1");
		tags.add("tag2");

		long userId = coreMockService.createMockUser(1);
		long nodeId = coreMockService.createMockRootNode(1);

		ArticlePayloadTO payload = new ArticlePayloadTO("mockArticleName",
				"[N1]Úvod[/N1][TAG]<strong>mockArticleText</strong>[/TAG] [N1]Konec[/N1] ende", tags, true,
				"mockContextRoot");
		long draftId = articleService.saveDraft(payload, nodeId, userId, false);

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
		assertTrue(articleTO.getPluginJSCodes().isEmpty());
		assertEquals("[N1]Úvod[/N1][TAG]<strong>mockArticleText</strong>[/TAG] [N1]Konec[/N1] ende",
				articleTO.getText());
		assertNull(articleTO.getOutputHTML());
		assertNull(articleTO.getSearchableOutput());
	}

	@Test
	public void testModifyDraft() {
		Set<String> tags = new HashSet<>();
		tags.add("tag1");
		tags.add("tag2");

		long userId = coreMockService.createMockUser(1);
		long nodeId = coreMockService.createMockRootNode(1);

		ArticlePayloadTO payload = new ArticlePayloadTO("mockArticleName",
				"[N1]Úvod[/N1][TAG]<strong>mockArticleText</strong>[/TAG] [N1]Konec[/N1] ende", tags, true,
				"mockContextRoot");
		Long draftId = articleService.saveDraft(payload, nodeId, userId, false);

		tags = new HashSet<>();
		tags.add("tag4");

		payload = new ArticlePayloadTO("mockArticleNameNew", "[N1]New[/N1] ende", tags, true, "mockContextRoot");
		articleService.modifyDraft(draftId, payload, false);

		ArticleTO articleTO = articleService.getArticleForDetail(draftId);
		assertNotNull(articleTO);
		assertEquals("mockArticleNameNew", articleTO.getContentNode().getName());
		assertEquals(new Long(nodeId), articleTO.getContentNode().getParent().getId());
		assertEquals(new Long(userId), articleTO.getContentNode().getAuthor().getId());
		assertEquals(new Long(draftId), articleTO.getContentNode().getContentID());
		assertEquals(ArticlesContentModule.ID, articleTO.getContentNode().getContentReaderID());
		assertEquals(1, articleTO.getContentNode().getContentTags().size());
		assertEquals("tag4", articleTO.getContentNode().getContentTags().iterator().next().getName());
		assertTrue(articleTO.getContentNode().isPublicated());
		assertTrue(articleTO.getContentNode().isDraft());
		assertNull(articleTO.getContentNode().getDraftSourceId());
		assertNotNull(articleTO.getContentNode().getLastModificationDate());
		assertNotNull(articleTO.getContentNode().getCreationDate());

		assertTrue(articleTO.getPluginCSSResources().isEmpty());
		assertTrue(articleTO.getPluginJSResources().isEmpty());
		assertTrue(articleTO.getPluginJSCodes().isEmpty());
		assertEquals("[N1]New[/N1] ende", articleTO.getText());
		assertNull(articleTO.getOutputHTML());
		assertNull(articleTO.getSearchableOutput());
	}

	@Test
	public void testReprocessDraft() throws InterruptedException, ExecutionException {
		Set<String> tags = new HashSet<>();
		tags.add("tag1");
		tags.add("tag2");

		long userId = coreMockService.createMockUser(1);
		long nodeId = coreMockService.createMockRootNode(1);

		ArticlePayloadTO payload = new ArticlePayloadTO("mockArticleName",
				"[N1]Úvod[/N1][TAG]<strong>mockArticleText</strong>[/TAG] [N1]Konec[/N1] ende", tags, true,
				"mockContextRoot");
		Long draftId = articleService.saveDraft(payload, nodeId, userId, false);

		tags = new HashSet<>();
		tags.add("tag4");

		payload = new ArticlePayloadTO("mockArticleNameNew", "[N1]New[/N1] ende", tags, true, "mockContextRoot");
		articleService.modifyDraft(draftId, payload, false);

		UUID operationId = UUID.randomUUID();

		ArticlesEventsHandler eventsHandler = new ArticlesEventsHandler();
		eventBus.subscribe(eventsHandler);
		CompletableFuture<ArticlesEventsHandler> future = eventsHandler.expectEvent(operationId);

		articleService.reprocessAllArticles(operationId, "mockContextRoot");

		ArticlesEventsHandler mock = future.get();
		ArticlesProcessResultEvent event = mock.getResultAndDelete(operationId);

		assertTrue(event.isSuccess());

		eventBus.unsubscribe(eventsHandler);

		ArticleTO articleTO = articleService.getArticleForDetail(draftId);
		assertNotNull(articleTO);
		assertEquals("mockArticleNameNew", articleTO.getContentNode().getName());
		assertEquals(new Long(nodeId), articleTO.getContentNode().getParent().getId());
		assertEquals(new Long(userId), articleTO.getContentNode().getAuthor().getId());
		assertEquals(new Long(draftId), articleTO.getContentNode().getContentID());
		assertEquals(ArticlesContentModule.ID, articleTO.getContentNode().getContentReaderID());
		assertEquals(1, articleTO.getContentNode().getContentTags().size());
		assertEquals("tag4", articleTO.getContentNode().getContentTags().iterator().next().getName());
		assertTrue(articleTO.getContentNode().isPublicated());
		assertTrue(articleTO.getContentNode().isDraft());
		assertNull(articleTO.getContentNode().getDraftSourceId());
		assertNotNull(articleTO.getContentNode().getLastModificationDate());
		assertNotNull(articleTO.getContentNode().getCreationDate());

		assertTrue(articleTO.getPluginCSSResources().isEmpty());
		assertTrue(articleTO.getPluginJSResources().isEmpty());
		assertTrue(articleTO.getPluginJSCodes().isEmpty());
		assertEquals("[N1]New[/N1] ende", articleTO.getText());
		assertNull(articleTO.getOutputHTML());
		assertNull(articleTO.getSearchableOutput());
	}

	@Test
	public void testModifyDraftAsPreview() {
		Set<String> tags = new HashSet<>();
		tags.add("tag1");
		tags.add("tag2");

		long userId = coreMockService.createMockUser(1);
		long nodeId = coreMockService.createMockRootNode(1);

		ArticlePayloadTO payload = new ArticlePayloadTO("mockArticleName",
				"[N1]Úvod[/N1][TAG]<strong>mockArticleText</strong>[/TAG] [N1]Konec[/N1] ende", tags, true,
				"mockContextRoot");
		Long draftId = articleService.saveDraft(payload, nodeId, userId, false);

		tags = new HashSet<>();
		tags.add("tag4");

		payload = new ArticlePayloadTO("mockArticleNameNew", "[N1]New[/N1] ende", tags, true, "mockContextRoot");
		articleService.modifyDraft(draftId, payload, true);

		ArticleTO articleTO = articleService.getArticleForDetail(draftId);
		assertNotNull(articleTO);
		assertEquals("mockArticleNameNew", articleTO.getContentNode().getName());
		assertEquals(new Long(nodeId), articleTO.getContentNode().getParent().getId());
		assertEquals(new Long(userId), articleTO.getContentNode().getAuthor().getId());
		assertEquals(new Long(draftId), articleTO.getContentNode().getContentID());
		assertEquals(ArticlesContentModule.ID, articleTO.getContentNode().getContentReaderID());
		assertEquals(1, articleTO.getContentNode().getContentTags().size());
		assertEquals("tag4", articleTO.getContentNode().getContentTags().iterator().next().getName());
		assertTrue(articleTO.getContentNode().isPublicated());
		assertTrue(articleTO.getContentNode().isDraft());
		assertNull(articleTO.getContentNode().getDraftSourceId());
		assertNotNull(articleTO.getContentNode().getLastModificationDate());
		assertNotNull(articleTO.getContentNode().getCreationDate());

		assertFalse(articleTO.getPluginCSSResources().isEmpty());
		assertTrue(articleTO.getPluginJSResources().isEmpty());
		assertTrue(articleTO.getPluginJSCodes().isEmpty());
		assertEquals("[N1]New[/N1] ende", articleTO.getText());
		assertEquals(
				"<div class=\"articles-h1\">New <a class=\"articles-h-id\" href=\"0\"></a></div><div class=\"level1\"> ende</div>",
				articleTO.getOutputHTML());
		assertNull(articleTO.getSearchableOutput());
	}

	@Test
	public void testSaveDraftOfExistingArticle() {
		Set<String> tags = new HashSet<>();
		tags.add("tag1");
		tags.add("tag2");

		long userId = coreMockService.createMockUser(1);
		long nodeId = coreMockService.createMockRootNode(1);
		ArticlePayloadTO payload = new ArticlePayloadTO("mockArticleName",
				"[N1]Úvod[/N1][TAG]<strong>mockArticleText</strong>[/TAG] [N2]Header2[/N2] dd [N3]Header3[/N3] ssaas [N4]Header4[/N4] fdd [N1]Konec[/N1] ende",
				tags, true, "mockContextRoot");
		Long articleId = articleService.saveArticle(payload, nodeId, userId);

		tags = new HashSet<>();
		tags.add("tag4");

		payload = new ArticlePayloadTO("mockArticleNameNew", "[N1]New[/N1] ende", tags, true, "mockContextRoot");
		Long draftId = articleService.saveDraftOfExistingArticle(payload, nodeId, userId, null, articleId, false);

		ArticleTO articleTO = articleService.getArticleForDetail(draftId);

		assertNotNull(articleTO);
		assertEquals("mockArticleNameNew", articleTO.getContentNode().getName());
		assertEquals(new Long(nodeId), articleTO.getContentNode().getParent().getId());
		assertEquals(new Long(userId), articleTO.getContentNode().getAuthor().getId());
		assertEquals(new Long(draftId), articleTO.getContentNode().getContentID());
		assertEquals(ArticlesContentModule.ID, articleTO.getContentNode().getContentReaderID());
		assertEquals(1, articleTO.getContentNode().getContentTags().size());
		assertEquals("tag4", articleTO.getContentNode().getContentTags().iterator().next().getName());
		assertTrue(articleTO.getContentNode().isPublicated());
		assertTrue(articleTO.getContentNode().isDraft());
		assertEquals(articleId, articleTO.getContentNode().getDraftSourceId());
		assertNull(articleTO.getContentNode().getLastModificationDate());
		assertNotNull(articleTO.getContentNode().getCreationDate());

		assertTrue(articleTO.getPluginCSSResources().isEmpty());
		assertTrue(articleTO.getPluginJSResources().isEmpty());
		assertTrue(articleTO.getPluginJSCodes().isEmpty());
		assertEquals("[N1]New[/N1] ende", articleTO.getText());
		assertNull(articleTO.getOutputHTML());
		assertNull(articleTO.getSearchableOutput());
	}

	@Test
	public void testModifyDraftOfExistingArticle() {
		Set<String> tags = new HashSet<>();
		tags.add("tag1");
		tags.add("tag2");

		long userId = coreMockService.createMockUser(1);
		long nodeId = coreMockService.createMockRootNode(1);
		ArticlePayloadTO payload = new ArticlePayloadTO("mockArticleName",
				"[N1]Úvod[/N1][TAG]<strong>mockArticleText</strong>[/TAG] [N2]Header2[/N2] dd [N3]Header3[/N3] ssaas [N4]Header4[/N4] fdd [N1]Konec[/N1] ende",
				tags, true, "mockContextRoot");
		Long articleId = articleService.saveArticle(payload, nodeId, userId);

		tags = new HashSet<>();
		tags.add("tag4");

		payload = new ArticlePayloadTO("mockArticleNameNew", "[N1]New[/N1] ende", tags, true, "mockContextRoot");
		Long draftId = articleService.saveDraftOfExistingArticle(payload, nodeId, userId, null, articleId, false);

		tags = new HashSet<>();
		tags.add("tag5");
		tags.add("tag6");
		tags.add("tag7");

		payload = new ArticlePayloadTO("mockArticleNameNew2", "[N1]New2[/N1] ende", tags, true, "mockContextRoot");
		articleService.modifyDraftOfExistingArticle(draftId, payload, null, articleId, false);

		ArticleTO articleTO = articleService.getArticleForDetail(draftId);

		assertNotNull(articleTO);
		assertEquals("mockArticleNameNew2", articleTO.getContentNode().getName());
		assertEquals(new Long(nodeId), articleTO.getContentNode().getParent().getId());
		assertEquals(new Long(userId), articleTO.getContentNode().getAuthor().getId());
		assertEquals(new Long(draftId), articleTO.getContentNode().getContentID());
		assertEquals(ArticlesContentModule.ID, articleTO.getContentNode().getContentReaderID());
		assertEquals(3, articleTO.getContentNode().getContentTags().size());
		List<String> arr = articleTO.getContentNode().getContentTags().stream().map(ContentTagOverviewTO::getName)
				.collect(Collectors.toList());
		assertTrue(arr.contains("tag5"));
		assertTrue(arr.contains("tag6"));
		assertTrue(arr.contains("tag7"));
		assertTrue(articleTO.getContentNode().isPublicated());
		assertTrue(articleTO.getContentNode().isDraft());
		assertEquals(articleId, articleTO.getContentNode().getDraftSourceId());
		assertNotNull(articleTO.getContentNode().getLastModificationDate());
		assertNotNull(articleTO.getContentNode().getCreationDate());

		assertTrue(articleTO.getPluginCSSResources().isEmpty());
		assertTrue(articleTO.getPluginJSResources().isEmpty());
		assertTrue(articleTO.getPluginJSCodes().isEmpty());
		assertEquals("[N1]New2[/N1] ende", articleTO.getText());
		assertNull(articleTO.getOutputHTML());
		assertNull(articleTO.getSearchableOutput());
	}

	@Test
	public void testReprocessDraftOfExistingArticle() throws InterruptedException, ExecutionException {
		Set<String> tags = new HashSet<>();
		tags.add("tag1");
		tags.add("tag2");

		long userId = coreMockService.createMockUser(1);
		long nodeId = coreMockService.createMockRootNode(1);
		ArticlePayloadTO payload = new ArticlePayloadTO("mockArticleName",
				"[N1]Úvod[/N1][TAG]<strong>mockArticleText</strong>[/TAG] [N2]Header2[/N2] dd [N3]Header3[/N3] ssaas [N4]Header4[/N4] fdd [N1]Konec[/N1] ende",
				tags, true, "mockContextRoot");
		Long articleId = articleService.saveArticle(payload, nodeId, userId);

		tags = new HashSet<>();
		tags.add("tag4");

		payload = new ArticlePayloadTO("mockArticleNameNew", "[N1]New[/N1] ende", tags, true, "mockContextRoot");
		Long draftId = articleService.saveDraftOfExistingArticle(payload, nodeId, userId, null, articleId, false);

		tags = new HashSet<>();
		tags.add("tag5");
		tags.add("tag6");
		tags.add("tag7");

		payload = new ArticlePayloadTO("mockArticleNameNew2", "[N1]New2[/N1] ende", tags, true, "mockContextRoot");
		articleService.modifyDraftOfExistingArticle(draftId, payload, null, articleId, false);

		UUID operationId = UUID.randomUUID();

		ArticlesEventsHandler eventsHandler = new ArticlesEventsHandler();
		eventBus.subscribe(eventsHandler);
		CompletableFuture<ArticlesEventsHandler> future = eventsHandler.expectEvent(operationId);

		articleService.reprocessAllArticles(operationId, "mockContextRoot");

		ArticlesEventsHandler mock = future.get();
		ArticlesProcessResultEvent event = mock.getResultAndDelete(operationId);

		assertTrue(event.isSuccess());

		eventBus.unsubscribe(eventsHandler);

		ArticleTO articleTO = articleService.getArticleForDetail(draftId);

		assertNotNull(articleTO);
		assertEquals("mockArticleNameNew2", articleTO.getContentNode().getName());
		assertEquals(new Long(nodeId), articleTO.getContentNode().getParent().getId());
		assertEquals(new Long(userId), articleTO.getContentNode().getAuthor().getId());
		assertEquals(new Long(draftId), articleTO.getContentNode().getContentID());
		assertEquals(ArticlesContentModule.ID, articleTO.getContentNode().getContentReaderID());
		assertEquals(3, articleTO.getContentNode().getContentTags().size());
		List<String> arr = articleTO.getContentNode().getContentTags().stream().map(ContentTagOverviewTO::getName)
				.collect(Collectors.toList());
		assertTrue(arr.contains("tag5"));
		assertTrue(arr.contains("tag6"));
		assertTrue(arr.contains("tag7"));
		assertTrue(articleTO.getContentNode().isPublicated());
		assertTrue(articleTO.getContentNode().isDraft());
		assertEquals(articleId, articleTO.getContentNode().getDraftSourceId());
		assertNotNull(articleTO.getContentNode().getLastModificationDate());
		assertNotNull(articleTO.getContentNode().getCreationDate());

		assertTrue(articleTO.getPluginCSSResources().isEmpty());
		assertTrue(articleTO.getPluginJSResources().isEmpty());
		assertTrue(articleTO.getPluginJSCodes().isEmpty());
		assertEquals("[N1]New2[/N1] ende", articleTO.getText());
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

		ArticlePayloadTO payload = new ArticlePayloadTO("mockArticleName", "mockArticleText", tags, true,
				"mockContextRoot");

		// preview
		long draftId = articleService.saveDraft(payload, nodeId, userId, true);

		// article
		articleService.saveArticle(payload, nodeId, userId);

		// draft
		long userId2 = coreMockService.createMockUser(2);
		long draftId2 = articleService.saveDraft(payload, nodeId, userId2, false);

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

		ArticlePayloadTO payload = new ArticlePayloadTO("mockArticleName", "mockArticleText", tags, true,
				"mockContextRoot");

		// preview
		long draftId = articleService.saveDraft(payload, nodeId, userId, true);

		// article
		articleService.saveArticle(payload, nodeId, userId);

		// draft
		long userId2 = coreMockService.createMockUser(2);
		long draftId2 = articleService.saveDraft(payload, nodeId, userId2, false);

		List<ArticleDraftOverviewTO> list = articleService.getDraftsForUser(null);
		assertEquals(0, list.size());

		list = articleService.getDraftsForUser(userId2);
		assertEquals(1, list.size());
		assertEquals(new Long(draftId2), list.get(0).getId());

		list = articleService.getDraftsForUser(userId);
		assertEquals(1, list.size());
		assertEquals(new Long(draftId), list.get(0).getId());

		Set<CoreRole> roles = new HashSet<>();
		roles.add(CoreRole.ADMIN);
		userService.changeUserRoles(userId, roles);

		list = articleService.getDraftsForUser(userId);
		assertEquals(2, list.size());
		assertEquals(new Long(draftId2), list.get(0).getId());
		assertEquals(new Long(draftId), list.get(1).getId());
	}

}
