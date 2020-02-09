package cz.gattserver.grass3.articles.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import cz.gattserver.grass3.articles.interfaces.ArticlePayloadTO;
import cz.gattserver.grass3.articles.interfaces.ArticleTO;
import cz.gattserver.grass3.modules.ArticlesContentModule;
import cz.gattserver.grass3.security.CoreRole;
import cz.gattserver.grass3.services.UserService;
import cz.gattserver.grass3.test.AbstractDBUnitTest;
import cz.gattserver.grass3.test.MockUtils;

@WebAppConfiguration
@DatabaseSetup(value = "deleteAll.xml", type = DatabaseOperation.DELETE_ALL)
public class ArticlesResourceTest extends AbstractDBUnitTest {

	@Autowired
	private ArticleService articleService;

	@Autowired
	private UserService userService;

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@Before
	public void init() throws Exception {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();
		SecurityContextHolder.getContext().setAuthentication(null);
	}

	private long prepareArticle() {
		Set<String> tags = new HashSet<>();
		tags.add("tag1");
		tags.add("tag2");

		long userId = coreMockService.createMockUser(1);
		long nodeId = coreMockService.createMockRootNode(1);
		ArticlePayloadTO payload = new ArticlePayloadTO("mockArticleName",
				"[N1]Úvod[/N1][TAG]<strong>mockArticleText</strong>[/TAG] [N2]Header2[/N2] dd [N3]Header3[/N3] ssaas [N4]Header4[/N4] fdd [N1]Konec[/N1] ende",
				tags, true, "mockContextRoot");
		return articleService.saveArticle(payload, nodeId, userId);
	}

	@Test
	public void testCount() throws Exception {
		prepareArticle();

		URI url = UriComponentsBuilder.fromUriString("/articles/count").build().encode().toUri();
		mockMvc.perform(get(url)).andExpect(status().isOk()).andExpect(content().string("1"));
	}

	@Test
	public void testCountNotPublished() throws Exception {
		long userId = coreMockService.createMockUser(1);
		long nodeId = coreMockService.createMockRootNode(1);

		ArticlePayloadTO payload = new ArticlePayloadTO("mockArticleName",
				"[N1]Úvod[/N1][TAG]<strong>mockArticleText</strong>[/TAG] [N2]Header2[/N2] dd [N3]Header3[/N3] ssaas [N4]Header4[/N4] fdd [N1]Konec[/N1] ende",
				new HashSet<>(), false, "mockContextRoot");
		articleService.saveArticle(payload, nodeId, userId);

		payload = new ArticlePayloadTO("mockArticleName2", "[N1]Test2[/N1]", new HashSet<>(), true, "mockContextRoot");
		articleService.saveArticle(payload, nodeId, userId);

		URI url = UriComponentsBuilder.fromUriString("/articles/count").build().encode().toUri();
		mockMvc.perform(get(url)).andExpect(status().isOk()).andExpect(content().string("1"));

		url = UriComponentsBuilder.fromUriString("/core/login").build().encode().toUri();
		mockMvc.perform(post(url).param("login", MockUtils.MOCK_USER_NAME + 1).param("password",
				MockUtils.MOCK_USER_PASSWORD + 1)).andExpect(status().isOk());

		url = UriComponentsBuilder.fromUriString("/articles/count").build().encode().toUri();
		mockMvc.perform(get(url)).andExpect(status().isOk()).andExpect(content().string("2"));
	}

	@Test
	public void testCountNotPublished2() throws Exception {
		long userId = coreMockService.createMockUser(1);
		long userId2 = coreMockService.createMockUser(2);
		long nodeId = coreMockService.createMockRootNode(1);

		ArticlePayloadTO payload = new ArticlePayloadTO("mockArticleName",
				"[N1]Úvod[/N1][TAG]<strong>mockArticleText</strong>[/TAG] [N2]Header2[/N2] dd [N3]Header3[/N3] ssaas [N4]Header4[/N4] fdd [N1]Konec[/N1] ende",
				new HashSet<>(), false, "mockContextRoot");
		articleService.saveArticle(payload, nodeId, userId);

		payload = new ArticlePayloadTO("mockArticleName2", "[N1]Test2[/N1]", new HashSet<>(), true, "mockContextRoot");
		articleService.saveArticle(payload, nodeId, userId2);

		URI url = UriComponentsBuilder.fromUriString("/articles/count").build().encode().toUri();
		mockMvc.perform(get(url)).andExpect(status().isOk()).andExpect(content().string("1"));

		url = UriComponentsBuilder.fromUriString("/core/login").build().encode().toUri();
		mockMvc.perform(post(url).param("login", MockUtils.MOCK_USER_NAME + 2).param("password",
				MockUtils.MOCK_USER_PASSWORD + 2)).andExpect(status().isOk());

		url = UriComponentsBuilder.fromUriString("/articles/count").build().encode().toUri();
		mockMvc.perform(get(url)).andExpect(status().isOk()).andExpect(content().string("1"));
	}

	@Test
	public void testCountNotPublishedAdmin() throws Exception {
		long userId = coreMockService.createMockUser(1);
		long userId2 = coreMockService.createMockUser(2);
		long nodeId = coreMockService.createMockRootNode(1);

		ArticlePayloadTO payload = new ArticlePayloadTO("mockArticleName",
				"[N1]Úvod[/N1][TAG]<strong>mockArticleText</strong>[/TAG] [N2]Header2[/N2] dd [N3]Header3[/N3] ssaas [N4]Header4[/N4] fdd [N1]Konec[/N1] ende",
				new HashSet<>(), false, "mockContextRoot");
		articleService.saveArticle(payload, nodeId, userId);

		payload = new ArticlePayloadTO("mockArticleName2", "[N1]Test2[/N1]", new HashSet<>(), true, "mockContextRoot");
		articleService.saveArticle(payload, nodeId, userId2);

		URI url = UriComponentsBuilder.fromUriString("/articles/count").build().encode().toUri();
		mockMvc.perform(get(url)).andExpect(status().isOk()).andExpect(content().string("1"));

		url = UriComponentsBuilder.fromUriString("/core/login").build().encode().toUri();
		mockMvc.perform(post(url).param("login", MockUtils.MOCK_USER_NAME + 2).param("password",
				MockUtils.MOCK_USER_PASSWORD + 2)).andExpect(status().isOk());

		url = UriComponentsBuilder.fromUriString("/articles/count").build().encode().toUri();
		mockMvc.perform(get(url)).andExpect(status().isOk()).andExpect(content().string("1"));

		Set<CoreRole> roles = new HashSet<>();
		roles.add(CoreRole.ADMIN);
		userService.changeUserRoles(userId2, roles);

		url = UriComponentsBuilder.fromUriString("/articles/count").build().encode().toUri();
		mockMvc.perform(get(url)).andExpect(status().isOk()).andExpect(content().string("2"));
	}

	@Test
	public void testList() throws Exception {
		prepareArticle();

		URI url = UriComponentsBuilder.fromUriString("/articles/list?page=0&pageSize=10").build().encode().toUri();
		mockMvc.perform(get(url)).andExpect(status().isOk())
				.andExpect(jsonPath("[0].contentReaderID").value("cz.gattserver.grass3.articles:0.0.1"))
				.andExpect(jsonPath("[0].contentID").exists()).andExpect(jsonPath("[0].name").value("mockArticleName"))
				.andExpect(jsonPath("[0].parentNodeName").value("mockNode1"))
				.andExpect(jsonPath("[0].parentNodeId").exists()).andExpect(jsonPath("[0].creationDate").isNotEmpty())
				// což znamená i null
				.andExpect(jsonPath("[0].lastModificationDate").doesNotExist())
				.andExpect(jsonPath("[0].publicated").value("true"))
				.andExpect(jsonPath("[0].authorName").value("mockUser1")).andExpect(jsonPath("[0].authorId").exists())
				.andExpect(jsonPath("[0].id").exists());
	}

	@Test
	public void testListNotPublished() throws Exception {
		long userId = coreMockService.createMockUser(1);
		long userId2 = coreMockService.createMockUser(2);
		long nodeId = coreMockService.createMockRootNode(1);

		ArticlePayloadTO payload = new ArticlePayloadTO("mockArticleName",
				"[N1]Úvod[/N1][TAG]<strong>mockArticleText</strong>[/TAG] [N2]Header2[/N2] dd [N3]Header3[/N3] ssaas [N4]Header4[/N4] fdd [N1]Konec[/N1] ende",
				new HashSet<>(), false, "mockContextRoot");
		Long article1 = articleService.saveArticle(payload, nodeId, userId);

		payload = new ArticlePayloadTO("mockArticleName2", "[N1]Test2[/N1]", new HashSet<>(), true, "mockContextRoot");
		Long article2 = articleService.saveArticle(payload, nodeId, userId2);

		URI url = UriComponentsBuilder.fromUriString("/articles/list?page=0&pageSize=10").build().encode().toUri();
		mockMvc.perform(get(url)).andExpect(status().isOk())
				.andExpect(jsonPath("[0].contentReaderID").value("cz.gattserver.grass3.articles:0.0.1"))
				.andExpect(jsonPath("[0].contentID").exists()).andExpect(jsonPath("[0].name").value("mockArticleName2"))
				.andExpect(jsonPath("[0].parentNodeName").value("mockNode1"))
				.andExpect(jsonPath("[0].parentNodeId").exists()).andExpect(jsonPath("[0].creationDate").isNotEmpty())
				// což znamená i null
				.andExpect(jsonPath("[0].lastModificationDate").doesNotExist())
				.andExpect(jsonPath("[0].publicated").value("true"))
				.andExpect(jsonPath("[0].authorName").value("mockUser2"))
				.andExpect(jsonPath("[0].authorId").value(userId2)).andExpect(jsonPath("[0].id").value(article2))
				.andExpect(jsonPath("[1]").doesNotExist());

		url = UriComponentsBuilder.fromUriString("/core/login").build().encode().toUri();
		mockMvc.perform(post(url).param("login", MockUtils.MOCK_USER_NAME + 1).param("password",
				MockUtils.MOCK_USER_PASSWORD + 1)).andExpect(status().isOk());

		url = UriComponentsBuilder.fromUriString("/articles/list?page=0&pageSize=10").build().encode().toUri();
		mockMvc.perform(get(url)).andExpect(status().isOk())
				// článek 1
				.andExpect(jsonPath("[0].contentReaderID").value("cz.gattserver.grass3.articles:0.0.1"))
				.andExpect(jsonPath("[0].contentID").exists()).andExpect(jsonPath("[0].name").value("mockArticleName2"))
				.andExpect(jsonPath("[0].parentNodeName").value("mockNode1"))
				.andExpect(jsonPath("[0].parentNodeId").exists()).andExpect(jsonPath("[0].creationDate").isNotEmpty())
				// což znamená i null
				.andExpect(jsonPath("[0].lastModificationDate").doesNotExist())
				.andExpect(jsonPath("[0].publicated").value("true"))
				.andExpect(jsonPath("[0].authorName").value("mockUser2"))
				.andExpect(jsonPath("[0].authorId").value(userId2)).andExpect(jsonPath("[0].id").value(article2))
				// článek 2
				.andExpect(jsonPath("[1].contentReaderID").value("cz.gattserver.grass3.articles:0.0.1"))
				.andExpect(jsonPath("[1].contentID").exists()).andExpect(jsonPath("[1].name").value("mockArticleName"))
				.andExpect(jsonPath("[1].parentNodeName").value("mockNode1"))
				.andExpect(jsonPath("[1].parentNodeId").exists()).andExpect(jsonPath("[1].creationDate").isNotEmpty())
				// což znamená i null
				.andExpect(jsonPath("[1].lastModificationDate").doesNotExist())
				.andExpect(jsonPath("[1].publicated").value("false"))
				.andExpect(jsonPath("[1].authorName").value("mockUser1"))
				.andExpect(jsonPath("[1].authorId").value(userId)).andExpect(jsonPath("[1].id").value(article1));
	}

	@Test
	public void testListNotPublished2() throws Exception {
		long userId = coreMockService.createMockUser(1);
		long nodeId = coreMockService.createMockRootNode(1);

		ArticlePayloadTO payload = new ArticlePayloadTO("mockArticleName",
				"[N1]Úvod[/N1][TAG]<strong>mockArticleText</strong>[/TAG] [N2]Header2[/N2] dd [N3]Header3[/N3] ssaas [N4]Header4[/N4] fdd [N1]Konec[/N1] ende",
				new HashSet<>(), false, "mockContextRoot");
		articleService.saveArticle(payload, nodeId, userId);

		payload = new ArticlePayloadTO("mockArticleName2", "[N1]Test2[/N1]", new HashSet<>(), true, "mockContextRoot");
		Long article2 = articleService.saveArticle(payload, nodeId, userId);

		URI url = UriComponentsBuilder.fromUriString("/articles/list?page=0&pageSize=10").build().encode().toUri();
		mockMvc.perform(get(url)).andExpect(status().isOk())
				.andExpect(jsonPath("[0].contentReaderID").value("cz.gattserver.grass3.articles:0.0.1"))
				.andExpect(jsonPath("[0].contentID").exists()).andExpect(jsonPath("[0].name").value("mockArticleName2"))
				.andExpect(jsonPath("[0].parentNodeName").value("mockNode1"))
				.andExpect(jsonPath("[0].parentNodeId").exists()).andExpect(jsonPath("[0].creationDate").isNotEmpty())
				// což znamená i null
				.andExpect(jsonPath("[0].lastModificationDate").doesNotExist())
				.andExpect(jsonPath("[0].publicated").value("true"))
				.andExpect(jsonPath("[0].authorName").value("mockUser1")).andExpect(jsonPath("[0].authorId").exists())
				.andExpect(jsonPath("[0].id").value(article2)).andExpect(jsonPath("[1]").doesNotExist());

		coreMockService.createMockUser(2);
		url = UriComponentsBuilder.fromUriString("/core/login").build().encode().toUri();
		mockMvc.perform(post(url).param("login", MockUtils.MOCK_USER_NAME + 2).param("password",
				MockUtils.MOCK_USER_PASSWORD + 2)).andExpect(status().isOk());

		url = UriComponentsBuilder.fromUriString("/articles/list?page=0&pageSize=10").build().encode().toUri();
		mockMvc.perform(get(url)).andExpect(status().isOk())
				.andExpect(jsonPath("[0].contentReaderID").value("cz.gattserver.grass3.articles:0.0.1"))
				.andExpect(jsonPath("[0].contentID").exists()).andExpect(jsonPath("[0].name").value("mockArticleName2"))
				.andExpect(jsonPath("[0].parentNodeName").value("mockNode1"))
				.andExpect(jsonPath("[0].parentNodeId").exists()).andExpect(jsonPath("[0].creationDate").isNotEmpty())
				// což znamená i null
				.andExpect(jsonPath("[0].lastModificationDate").doesNotExist())
				.andExpect(jsonPath("[0].publicated").value("true"))
				.andExpect(jsonPath("[0].authorName").value("mockUser1")).andExpect(jsonPath("[0].authorId").exists())
				.andExpect(jsonPath("[0].id").value(article2)).andExpect(jsonPath("[1]").doesNotExist());
	}

	@Test
	public void testListBadRequest() throws Exception {
		prepareArticle();

		URI url = UriComponentsBuilder.fromUriString("/articles/list?page=1&pageSize=10").build().encode().toUri();
		mockMvc.perform(get(url)).andExpect(status().isBadRequest());
	}

	@Test
	public void testCreateForbidden() throws Exception {
		URI url = UriComponentsBuilder.fromUriString("/articles/create").build().encode().toUri();
		mockMvc.perform(post(url).param("text", "[N1]Úvod[/N1][TAG]<strong>mockArticleText</strong>[/TAG]"))
				.andExpect(status().isForbidden());
	}

	@Test
	public void testCreate() throws Exception {
		long userId = coreMockService.createMockUser(1);
		long nodeId = coreMockService.createMockRootNode(1);

		URI url = UriComponentsBuilder.fromUriString("/core/login").build().encode().toUri();
		mockMvc.perform(post(url).param("login", MockUtils.MOCK_USER_NAME + 1).param("password",
				MockUtils.MOCK_USER_PASSWORD + 1)).andExpect(status().isOk());

		url = UriComponentsBuilder.fromUriString("/articles/create").build().encode().toUri();
		String mvcResult = mockMvc
				.perform(post(url).param("text", "[N1]Úvod[/N1][TAG]<strong>mockArticleText</strong>[/TAG]"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		Long articleId = Long.valueOf(mvcResult);
		assertNotNull(articleId);

		ArticleTO articleTO = articleService.getArticleForDetail(articleId);
		assertNotNull(articleTO);
		assertTrue(articleTO.getContentNode().getName().startsWith("SMS Import"));
		assertEquals(new Long(nodeId), articleTO.getContentNode().getParent().getId());
		assertEquals(new Long(userId), articleTO.getContentNode().getAuthor().getId());
		assertEquals(new Long(articleId), articleTO.getContentNode().getContentID());
		assertEquals(ArticlesContentModule.ID, articleTO.getContentNode().getContentReaderID());
		assertEquals(0, articleTO.getContentNode().getContentTags().size());
		assertFalse(articleTO.getContentNode().isPublicated());
		assertFalse(articleTO.getContentNode().isDraft());
		assertNull(articleTO.getContentNode().getDraftSourceId());
		assertNull(articleTO.getContentNode().getLastModificationDate());
		assertNotNull(articleTO.getContentNode().getCreationDate());

		assertFalse(articleTO.getPluginCSSResources().isEmpty());
		assertTrue(articleTO.getPluginJSResources().isEmpty());
		assertTrue(articleTO.getPluginJSCodes().isEmpty());
		assertEquals("[N1]Úvod[/N1][TAG]<strong>mockArticleText</strong>[/TAG]", articleTO.getText());
		assertEquals(
				"<div class=\"articles-h1\">Úvod <a class=\"articles-h-id\" href=\"0\"></a></div><div class=\"level1\">[TAG]&lt;strong&gt;mockArticleText&lt;/strong&gt;[/TAG]</div>",
				articleTO.getOutputHTML());
		assertNull(articleTO.getSearchableOutput());
	}

	@Test
	public void testArticle() throws Exception {
		long articleId = prepareArticle();

		URI url = UriComponentsBuilder.fromUriString("/articles/article").build().encode().toUri();
		mockMvc.perform(get(url).param("id", String.valueOf(articleId))).andExpect(status().isOk())
				.andExpect(jsonPath("contentNode.contentReaderID").value("cz.gattserver.grass3.articles:0.0.1"))
				.andExpect(jsonPath("contentNode.contentID").exists())
				.andExpect(jsonPath("contentNode.name").value("mockArticleName"))
				.andExpect(jsonPath("contentNode.parent.name").value("mockNode1"))
				.andExpect(jsonPath("contentNode.parent.id").exists())
				.andExpect(jsonPath("contentNode.creationDate").isNotEmpty())
				// což znamená i null
				.andExpect(jsonPath("contentNode.lastModificationDate").doesNotExist())
				.andExpect(jsonPath("contentNode.publicated").value("true"))
				.andExpect(jsonPath("contentNode.author.name").value("mockUser1"))
				.andExpect(jsonPath("contentNode.author.id").exists()).andExpect(jsonPath("contentNode.id").exists())
				.andExpect(jsonPath("outputHTML")
						.value("<div class=\"articles-h1\">Úvod <a class=\"articles-h-id\" href=\"0\"></a></div><div class=\"level1\">"
								+ "[TAG]&lt;strong&gt;mockArticleText&lt;/strong&gt;[/TAG] </div><div class=\"articles-h2\">Header2 <a class=\"articles-h-id\" href=\"1\"></a>"
								+ "</div><div class=\"level2\"> dd </div><div class=\"articles-h3\">Header3 <a class=\"articles-h-id\" href=\"2\"></a></div>"
								+ "<div class=\"level3\"> ssaas </div><div class=\"articles-h4\">Header4 <a class=\"articles-h-id\" href=\"3\"></a></div><div class=\"level4\"> fdd </div>"
								+ "<div class=\"articles-h1\">Konec <a class=\"articles-h-id\" href=\"4\"></a></div><div class=\"level1\"> ende</div>"));
	}

	@Test
	public void testArticleMissing() throws Exception {
		URI url = UriComponentsBuilder.fromUriString("/articles/article").build().encode().toUri();
		mockMvc.perform(get(url).param("id", "99999")).andExpect(status().isNotFound());
	}

	@Test
	public void testArticleAuthorVisibility() throws Exception {
		Set<String> tags = new HashSet<>();
		tags.add("tag1");
		tags.add("tag2");

		long userId = coreMockService.createMockUser(1);
		long nodeId = coreMockService.createMockRootNode(1);
		ArticlePayloadTO payload = new ArticlePayloadTO("mockArticleName",
				"[N1]Úvod[/N1][TAG]<strong>mockArticleText</strong>[/TAG] [N2]Header2[/N2] dd [N3]Header3[/N3] ssaas [N4]Header4[/N4] fdd [N1]Konec[/N1] ende",
				tags, false, "mockContextRoot");
		Long articleId = articleService.saveArticle(payload, nodeId, userId);

		URI url = UriComponentsBuilder.fromUriString("/articles/article").build().encode().toUri();
		mockMvc.perform(get(url).param("id", String.valueOf(articleId))).andExpect(status().isForbidden());

		url = UriComponentsBuilder.fromUriString("/core/login").build().encode().toUri();
		mockMvc.perform(post(url).param("login", MockUtils.MOCK_USER_NAME + 1).param("password",
				MockUtils.MOCK_USER_PASSWORD + 1)).andExpect(status().isOk());

		url = UriComponentsBuilder.fromUriString("/articles/article").build().encode().toUri();
		mockMvc.perform(get(url).param("id", String.valueOf(articleId))).andExpect(status().isOk())
				.andExpect(jsonPath("contentNode.contentReaderID").value("cz.gattserver.grass3.articles:0.0.1"))
				.andExpect(jsonPath("contentNode.contentID").exists())
				.andExpect(jsonPath("contentNode.name").value("mockArticleName"))
				.andExpect(jsonPath("contentNode.parent.name").value("mockNode1"))
				.andExpect(jsonPath("contentNode.parent.id").exists())
				.andExpect(jsonPath("contentNode.creationDate").isNotEmpty())
				// což znamená i null
				.andExpect(jsonPath("contentNode.lastModificationDate").doesNotExist())
				.andExpect(jsonPath("contentNode.publicated").value("false"))
				.andExpect(jsonPath("contentNode.author.name").value("mockUser1"))
				.andExpect(jsonPath("contentNode.author.id").exists()).andExpect(jsonPath("contentNode.id").exists())
				.andExpect(jsonPath("outputHTML")
						.value("<div class=\"articles-h1\">Úvod <a class=\"articles-h-id\" href=\"0\"></a></div><div class=\"level1\">"
								+ "[TAG]&lt;strong&gt;mockArticleText&lt;/strong&gt;[/TAG] </div><div class=\"articles-h2\">Header2 <a class=\"articles-h-id\" href=\"1\"></a>"
								+ "</div><div class=\"level2\"> dd </div><div class=\"articles-h3\">Header3 <a class=\"articles-h-id\" href=\"2\"></a></div>"
								+ "<div class=\"level3\"> ssaas </div><div class=\"articles-h4\">Header4 <a class=\"articles-h-id\" href=\"3\"></a></div><div class=\"level4\"> fdd </div>"
								+ "<div class=\"articles-h1\">Konec <a class=\"articles-h-id\" href=\"4\"></a></div><div class=\"level1\"> ende</div>"));
	}

	@Test
	public void testArticleAdminVisibility() throws Exception {
		Set<String> tags = new HashSet<>();
		tags.add("tag1");
		tags.add("tag2");

		long userId = coreMockService.createMockUser(1);
		long nodeId = coreMockService.createMockRootNode(1);
		ArticlePayloadTO payload = new ArticlePayloadTO("mockArticleName",
				"[N1]Úvod[/N1][TAG]<strong>mockArticleText</strong>[/TAG] [N2]Header2[/N2] dd [N3]Header3[/N3] ssaas [N4]Header4[/N4] fdd [N1]Konec[/N1] ende",
				tags, false, "mockContextRoot");
		Long articleId = articleService.saveArticle(payload, nodeId, userId);

		URI url = UriComponentsBuilder.fromUriString("/articles/article").build().encode().toUri();
		mockMvc.perform(get(url).param("id", String.valueOf(articleId))).andExpect(status().isForbidden());

		long userId2 = coreMockService.createMockUser(2);

		url = UriComponentsBuilder.fromUriString("/core/login").build().encode().toUri();
		mockMvc.perform(post(url).param("login", MockUtils.MOCK_USER_NAME + 2).param("password",
				MockUtils.MOCK_USER_PASSWORD + 2)).andExpect(status().isOk());

		url = UriComponentsBuilder.fromUriString("/articles/article").build().encode().toUri();
		mockMvc.perform(get(url).param("id", String.valueOf(articleId))).andExpect(status().isForbidden());

		Set<CoreRole> roles = new HashSet<>();
		roles.add(CoreRole.ADMIN);
		userService.changeUserRoles(userId2, roles);

		url = UriComponentsBuilder.fromUriString("/articles/article").build().encode().toUri();
		mockMvc.perform(get(url).param("id", String.valueOf(articleId))).andExpect(status().isOk())
				.andExpect(jsonPath("contentNode.contentReaderID").value("cz.gattserver.grass3.articles:0.0.1"))
				.andExpect(jsonPath("contentNode.contentID").exists())
				.andExpect(jsonPath("contentNode.name").value("mockArticleName"))
				.andExpect(jsonPath("contentNode.parent.name").value("mockNode1"))
				.andExpect(jsonPath("contentNode.parent.id").exists())
				.andExpect(jsonPath("contentNode.creationDate").isNotEmpty())
				// což znamená i null
				.andExpect(jsonPath("contentNode.lastModificationDate").doesNotExist())
				.andExpect(jsonPath("contentNode.publicated").value("false"))
				.andExpect(jsonPath("contentNode.author.name").value("mockUser1"))
				.andExpect(jsonPath("contentNode.author.id").exists()).andExpect(jsonPath("contentNode.id").exists())
				.andExpect(jsonPath("outputHTML")
						.value("<div class=\"articles-h1\">Úvod <a class=\"articles-h-id\" href=\"0\"></a></div><div class=\"level1\">"
								+ "[TAG]&lt;strong&gt;mockArticleText&lt;/strong&gt;[/TAG] </div><div class=\"articles-h2\">Header2 <a class=\"articles-h-id\" href=\"1\"></a>"
								+ "</div><div class=\"level2\"> dd </div><div class=\"articles-h3\">Header3 <a class=\"articles-h-id\" href=\"2\"></a></div>"
								+ "<div class=\"level3\"> ssaas </div><div class=\"articles-h4\">Header4 <a class=\"articles-h-id\" href=\"3\"></a></div><div class=\"level4\"> fdd </div>"
								+ "<div class=\"articles-h1\">Konec <a class=\"articles-h-id\" href=\"4\"></a></div><div class=\"level1\"> ende</div>"));
	}

	@Test
	public void testArticleForbidden() throws Exception {
		Set<String> tags = new HashSet<>();
		tags.add("tag1");
		tags.add("tag2");

		long userId = coreMockService.createMockUser(1);
		long nodeId = coreMockService.createMockRootNode(1);
		ArticlePayloadTO payload = new ArticlePayloadTO("mockArticleName",
				"[N1]Úvod[/N1][TAG]<strong>mockArticleText</strong>[/TAG] [N2]Header2[/N2] dd [N3]Header3[/N3] ssaas [N4]Header4[/N4] fdd [N1]Konec[/N1] ende",
				tags, false, "mockContextRoot");
		Long articleId = articleService.saveArticle(payload, nodeId, userId);

		URI url = UriComponentsBuilder.fromUriString("/articles/article").build().encode().toUri();
		mockMvc.perform(get(url).param("id", String.valueOf(articleId))).andExpect(status().isForbidden());
	}
}
