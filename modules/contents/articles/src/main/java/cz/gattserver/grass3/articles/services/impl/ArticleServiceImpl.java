package cz.gattserver.grass3.articles.services.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass3.articles.editor.lexer.Lexer;
import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.PluginBag;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.impl.ArticleParser;
import cz.gattserver.grass3.articles.editor.parser.impl.ContextImpl;
import cz.gattserver.grass3.articles.editor.parser.util.HTMLTagsFilter;
import cz.gattserver.grass3.articles.events.impl.ArticlesProcessProgressEvent;
import cz.gattserver.grass3.articles.events.impl.ArticlesProcessResultEvent;
import cz.gattserver.grass3.articles.events.impl.ArticlesProcessStartEvent;
import cz.gattserver.grass3.articles.interfaces.ArticleDraftOverviewTO;
import cz.gattserver.grass3.articles.interfaces.ArticleTO;
import cz.gattserver.grass3.articles.model.domain.Article;
import cz.gattserver.grass3.articles.model.domain.ArticleJSResource;
import cz.gattserver.grass3.articles.model.repositories.ArticleRepository;
import cz.gattserver.grass3.articles.model.util.ArticlesMapper;
import cz.gattserver.grass3.articles.services.ArticleProcessMode;
import cz.gattserver.grass3.articles.services.ArticleService;
import cz.gattserver.grass3.events.EventBus;
import cz.gattserver.grass3.model.domain.ContentNode;
import cz.gattserver.grass3.model.domain.ContentTag;
import cz.gattserver.grass3.model.repositories.UserRepository;
import cz.gattserver.grass3.modules.ArticlesContentModule;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.services.ContentNodeService;

@Transactional
@Component
public class ArticleServiceImpl implements ArticleService {

	@Autowired
	private EventBus eventBus;

	@Autowired
	private ContentNodeService contentNodeFacade;

	@Autowired
	private ArticlesMapper articlesMapper;

	@Autowired
	private ArticleRepository articleRepository;

	@Autowired
	private UserRepository userRepository;

	private Context processArticle(String source, String contextRoot) {

		if (contextRoot == null)
			throw new IllegalArgumentException("ContextRoot nemůže být null");

		Lexer lexer = new Lexer(source);
		Parser parser = new ArticleParser();
		PluginBag pluginBag = new PluginBag(lexer, contextRoot);

		// výstup
		Element tree = parser.parse(pluginBag);
		Context ctx = new ContextImpl();
		tree.apply(ctx);

		return ctx;
	}

	@Override
	public void deleteArticle(long id) {
		// smaž článek
		articleRepository.delete(id);

		// smaž jeho content node
		contentNodeFacade.deleteByContentId(ArticlesContentModule.ID, id);
	}

	private SortedSet<ArticleJSResource> createJSResourcesSet(Set<String> scripts) {
		int order = 0;
		SortedSet<ArticleJSResource> set = new TreeSet<>();
		for (String string : scripts) {
			ArticleJSResource resource = new ArticleJSResource();
			resource.setName(string);
			resource.setExecutionOrder(order++);
			set.add(resource);
		}
		return set;
	}

	@Override
	public long saveArticle(String name, String text, Collection<String> tags, boolean publicated, long nodeId,
			long authorId, String contextRoot, ArticleProcessMode processForm, Long existingId) {
		return saveArticle(name, text, tags, publicated, nodeId, authorId, contextRoot, processForm, existingId, null,
				null);
	}

	@Override
	public long saveArticle(String name, String text, Collection<String> tags, boolean publicated, long nodeId,
			long authorId, String contextRoot, ArticleProcessMode processForm, Long existingId, Integer partNumber,
			Long draftSourceId) {

		// Flags
		boolean process = false;
		boolean draft = false;
		switch (processForm) {
		case DRAFT:
			draft = true;
			break;
		case PREVIEW:
			draft = true;
		case FULL:
			process = true;
		}

		Article article;
		if (existingId == null) {
			// vytvoř nový článek
			article = new Article();
			if (draft) {
				article.setPartNumber(partNumber);
			}
		} else {
			article = articleRepository.findOne(existingId);
		}

		// nasetuj do něj vše potřebné
		if (process) {
			Context context = processArticle(text, contextRoot);
			article.setOutputHTML(context.getOutput());
			article.setPluginCSSResources(context.getCSSResources());
			article.setPluginJSResources(createJSResourcesSet(context.getJSResources()));
			article.setSearchableOutput(HTMLTagsFilter.trim(context.getOutput()));
		}
		article.setText(text);

		// ulož ho a nasetuj jeho id
		article = articleRepository.save(article);

		if (existingId == null) {
			// vytvoř odpovídající content node
			Long contentNodeId = contentNodeFacade.save(ArticlesContentModule.ID, article.getId(), name, tags,
					publicated, nodeId, authorId, draft, null, draftSourceId);

			// ulož do článku referenci na jeho contentnode
			ContentNode contentNode = new ContentNode();
			contentNode.setId(contentNodeId);
			article.setContentNode(contentNode);
			articleRepository.save(article);
		} else {
			contentNodeFacade.modify(article.getContentNode().getId(), name, tags, publicated);
		}

		return article.getId();
	}

	@Override
	public ArticleTO getArticleForDetail(long id) {
		Article article = articleRepository.findOne(id);
		if (article == null)
			return null;
		ArticleTO articleDTO = articlesMapper.mapArticleForDetail(article);
		return articleDTO;
	}

	@Async
	@Override
	public void reprocessAllArticles(String contextRoot) {

		List<Article> articles = articleRepository.findAll();
		int total = articles.size();

		// Počet kroků = miniatury + detaily + uložení
		eventBus.publish(new ArticlesProcessStartEvent(total));

		int current = 0;
		for (Article article : articles) {

			Collection<ContentTag> tagsDTOs = article.getContentNode().getContentTags();

			Set<String> tags = new HashSet<String>();
			for (ContentTag tag : tagsDTOs)
				tags.add(tag.getName());

			ArticleProcessMode articleProcessForm = Boolean.TRUE.equals(article.getContentNode().getDraft())
					? ArticleProcessMode.PREVIEW : ArticleProcessMode.FULL;
			saveArticle(article.getContentNode().getName(), article.getText(), tags,
					article.getContentNode().getPublicated(), article.getContentNode().getId(),
					article.getContentNode().getAuthor().getId(), contextRoot, articleProcessForm, article.getId(),
					article.getPartNumber(), article.getContentNode().getDraftSourceId());

			eventBus.publish(new ArticlesProcessProgressEvent(
					"(" + current + "/" + total + ") " + article.getContentNode().getName()));
			current++;
		}

		eventBus.publish(new ArticlesProcessResultEvent());
	}

	@Override
	public List<ArticleTO> getAllArticlesForSearch() {
		List<Article> articles = articleRepository.findAll();
		if (articles == null)
			return null;
		List<ArticleTO> articleDTOs = articlesMapper.mapArticlesForSearch(articles);
		return articleDTOs;
	}

	@Override
	public List<ArticleDraftOverviewTO> getDraftsForUser(long userId) {
		boolean isAdmin = userRepository.hasRole(userId, Role.ADMIN);
		List<Article> articles = articleRepository.findDraftsForUser(userId, isAdmin);
		if (articles == null)
			return null;
		List<ArticleDraftOverviewTO> articleDTOs = articlesMapper.mapArticlesForDraftOverview(articles);
		return articleDTOs;
	}

}
