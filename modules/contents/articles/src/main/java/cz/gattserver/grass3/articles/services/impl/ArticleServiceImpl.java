package cz.gattserver.grass3.articles.services.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass3.articles.editor.lexer.Lexer;
import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.impl.ArticleParser;
import cz.gattserver.grass3.articles.editor.parser.impl.ContextImpl;
import cz.gattserver.grass3.articles.editor.parser.util.HTMLTagsFilter;
import cz.gattserver.grass3.articles.events.impl.ArticlesProcessProgressEvent;
import cz.gattserver.grass3.articles.events.impl.ArticlesProcessResultEvent;
import cz.gattserver.grass3.articles.events.impl.ArticlesProcessStartEvent;
import cz.gattserver.grass3.articles.interfaces.ArticleDraftOverviewTO;
import cz.gattserver.grass3.articles.interfaces.ArticlePayloadTO;
import cz.gattserver.grass3.articles.interfaces.ArticleRESTTO;
import cz.gattserver.grass3.articles.interfaces.ArticleTO;
import cz.gattserver.grass3.articles.model.domain.Article;
import cz.gattserver.grass3.articles.model.domain.ArticleJSCode;
import cz.gattserver.grass3.articles.model.domain.ArticleJSResource;
import cz.gattserver.grass3.articles.model.repositories.ArticleRepository;
import cz.gattserver.grass3.articles.plugins.register.PluginRegisterService;
import cz.gattserver.grass3.articles.services.ArticleService;
import cz.gattserver.grass3.articles.services.ArticlesMapperService;
import cz.gattserver.grass3.events.EventBus;
import cz.gattserver.grass3.exception.UnauthorizedAccessException;
import cz.gattserver.grass3.model.domain.ContentNode;
import cz.gattserver.grass3.model.domain.ContentTag;
import cz.gattserver.grass3.model.domain.User;
import cz.gattserver.grass3.model.repositories.UserRepository;
import cz.gattserver.grass3.modules.ArticlesContentModule;
import cz.gattserver.grass3.services.ContentNodeService;

@Transactional
@Service
public class ArticleServiceImpl implements ArticleService {

	@Autowired
	private EventBus eventBus;

	@Autowired
	private ContentNodeService contentNodeFacade;

	@Autowired
	private ArticlesMapperService articlesMapper;

	@Autowired
	private ArticleRepository articleRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PluginRegisterService pluginRegister;

	private Context processArticle(String source, String contextRoot) {
		Validate.notNull(contextRoot, "ContextRoot nemůže být null");

		Lexer lexer = new Lexer(source);
		Parser parser = new ArticleParser();
		ParsingProcessor parsingProcessor = new ParsingProcessor(lexer, contextRoot, pluginRegister.createRegisterSnapshot());

		// výstup
		Element tree = parser.parse(parsingProcessor);
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
		for (String name : scripts) {
			ArticleJSResource resource = new ArticleJSResource();
			resource.setName(name);
			resource.setExecutionOrder(order++);
			set.add(resource);
		}
		return set;
	}

	private SortedSet<ArticleJSCode> createJSCodesSet(Set<String> contents) {
		int order = 0;
		SortedSet<ArticleJSCode> set = new TreeSet<>();
		for (String content : contents) {
			ArticleJSCode resource = new ArticleJSCode();
			resource.setContent(content);
			resource.setExecutionOrder(order++);
			set.add(resource);
		}
		return set;
	}

	@Override
	public long saveArticle(ArticlePayloadTO payload, long nodeId, long authorId) {
		return innerSaveArticle(payload, nodeId, authorId, true, false, null, null, null);
	}

	@Override
	public void modifyArticle(long articleId, ArticlePayloadTO payload, Integer partNumber) {
		innerSaveArticle(payload, null, null, true, false, articleId, partNumber, null);
	}

	@Override
	public long saveDraft(ArticlePayloadTO payload, long nodeId, long authorId, boolean asPreview) {
		return innerSaveArticle(payload, nodeId, authorId, asPreview, true, null, null, null);
	}

	@Override
	public long saveDraftOfExistingArticle(ArticlePayloadTO payload, long nodeId, long authorId, Integer partNumber,
			long originArticleId, boolean asPreview) {
		return innerSaveArticle(payload, nodeId, authorId, asPreview, true, null, partNumber, originArticleId);
	}

	@Override
	public void modifyDraft(long drafId, ArticlePayloadTO payload, boolean asPreview) {
		innerSaveArticle(payload, null, null, asPreview, true, drafId, null, null);
	}

	@Override
	public void modifyDraftOfExistingArticle(long drafId, ArticlePayloadTO payload, Integer partNumber,
			long originArticleId, boolean asPreview) {
		innerSaveArticle(payload, null, null, asPreview, true, drafId, partNumber, originArticleId);
	}

	private long innerSaveArticle(ArticlePayloadTO payload, Long nodeId, Long authorId, boolean process, boolean draft,
			Long existingId, Integer partNumber, Long draftSourceId) {

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
			Context context = processArticle(payload.getText(), payload.getContextRoot());
			article.setOutputHTML(context.getOutput());
			article.setPluginCSSResources(context.getCSSResources());
			article.setPluginJSResources(createJSResourcesSet(context.getJSResources()));
			article.setPluginJSCodes(createJSCodesSet(context.getJSCodes()));
			article.setSearchableOutput(HTMLTagsFilter.trim(context.getOutput()));
		}
		article.setText(payload.getText());

		// ulož ho a nasetuj jeho id
		article = articleRepository.save(article);

		if (existingId == null) {
			// vytvoř odpovídající content node
			Long contentNodeId = contentNodeFacade.save(ArticlesContentModule.ID, article.getId(), payload.getName(),
					payload.getTags(), payload.isPublicated(), nodeId, authorId, draft, null, draftSourceId);

			// ulož do článku referenci na jeho contentnode
			ContentNode contentNode = new ContentNode();
			contentNode.setId(contentNodeId);
			article.setContentNode(contentNode);
			articleRepository.save(article);
		} else {
			contentNodeFacade.modify(article.getContentNode().getId(), payload.getName(), payload.getTags(),
					payload.isPublicated());
		}

		return article.getId();
	}

	@Override
	public ArticleTO getArticleForDetail(long id) {
		Article article = articleRepository.findOne(id);
		if (article == null)
			return null;
		return articlesMapper.mapArticleForDetail(article);
	}

	@Override
	public ArticleRESTTO getArticleForREST(Long id, Long userId) throws UnauthorizedAccessException {
		Article article = articleRepository.findOne(id);
		if (article == null)
			return null;
		User user = userId == null ? null : userRepository.findOne(userId);
		if (article.getContentNode().getPublicated() || user != null
				&& (user.isAdmin() || article.getContentNode().getAuthor().getId().equals(user.getId()))) {
			return articlesMapper.mapArticleForREST(article);
		}
		throw new UnauthorizedAccessException();
	}

	@Async
	@Override
	public void reprocessAllArticles(UUID operationId, String contextRoot) {
		int total = (int) articleRepository.count();
		eventBus.publish(new ArticlesProcessStartEvent(total));
		int current = 0;
		int pageSize = 100;
		int pages = (int) Math.ceil(total * 1.0 / pageSize);
		for (int page = 0; page < pages; page++) {
			List<Article> articles = articleRepository.findAll(new PageRequest(page, pageSize)).getContent();
			for (Article article : articles) {
				reprocessArticle(article, contextRoot);
				eventBus.publish(new ArticlesProcessProgressEvent(
						"(" + current + "/" + total + ") " + article.getContentNode().getName()));
				current++;
			}
		}

		eventBus.publish(new ArticlesProcessResultEvent(operationId));
	}

	private void reprocessArticle(Article article, String contextRoot) {
		Collection<ContentTag> tagsDTOs = article.getContentNode().getContentTags();
		Set<String> tags = new HashSet<>();
		for (ContentTag tag : tagsDTOs)
			tags.add(tag.getName());

		ArticlePayloadTO payload = new ArticlePayloadTO(article.getContentNode().getName(), article.getText(), tags,
				article.getContentNode().getPublicated(), contextRoot);
		if (article.getContentNode().getDraft() == null || article.getContentNode().getDraft()) {
			if (article.getContentNode().getDraftSourceId() != null) {
				modifyDraftOfExistingArticle(article.getId(), payload, null,
						article.getContentNode().getDraftSourceId(), false);
			} else {
				modifyDraft(article.getId(), payload, false);
			}
		} else {
			modifyArticle(article.getId(), payload, null);
		}
	}

	@Override
	public List<ArticleDraftOverviewTO> getDraftsForUser(Long userId) {
		boolean isAdmin = userId == null ? false : userRepository.findOne(userId).isAdmin();
		List<Article> articles = articleRepository.findDraftsForUser(userId, isAdmin);
		return articlesMapper.mapArticlesForDraftOverview(articles);
	}

}
