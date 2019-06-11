package cz.gattserver.grass3.articles.services;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import cz.gattserver.grass3.articles.interfaces.ArticlePayloadTO;
import cz.gattserver.grass3.services.UserService;
import cz.gattserver.grass3.test.AbstractDBUnitTest;

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
	public void init() {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();
	}

	private void prepareArticle() {
		Set<String> tags = new HashSet<>();
		tags.add("tag1");
		tags.add("tag2");

		long userId = coreMockService.createMockUser(1);
		long nodeId = coreMockService.createMockRootNode(1);
		ArticlePayloadTO payload = new ArticlePayloadTO("mockArticleName",
				"[N1]Úvod[/N1][TAG]<strong>mockArticleText</strong>[/TAG] [N2]Header2[/N2] dd [N3]Header3[/N3] ssaas [N4]Header4[/N4] fdd [N1]Konec[/N1] ende",
				tags, true, "mockContextRoot");
		articleService.saveArticle(payload, nodeId, userId);
	}

	@Test
	public void testCount() throws Exception {

		prepareArticle();

		URI url = UriComponentsBuilder.fromUriString("/articles/count").build().encode().toUri();
		mockMvc.perform(get(url)).andExpect(status().isOk()).andExpect(content().string("1"));

		url = UriComponentsBuilder.fromUriString("/articles/list?page=0&pageSize=10").build().encode().toUri();
		mockMvc.perform(get(url)).andExpect(status().isOk())
				.andExpect(jsonPath("[0].contentReaderID").value("cz.gattserver.grass3.articles:0.0.1"))
				.andExpect(jsonPath("[0].contentID").value("1"))
				.andExpect(jsonPath("[0].name").value("mockArticleName"))
				.andExpect(jsonPath("[0].parentNodeName").value("mockNode1"))
				.andExpect(jsonPath("[0].parentNodeId").value("1")).andExpect(jsonPath("[0].creationDate").isNotEmpty())
				// což znamená i null
				.andExpect(jsonPath("[0].lastModificationDate").doesNotExist())
				.andExpect(jsonPath("[0].publicated").value("true"))
				.andExpect(jsonPath("[0].authorName").value("mockUser1")).andExpect(jsonPath("[0].authorId").value("1"))
				.andExpect(jsonPath("[0].id").value("1"));
	}

}
