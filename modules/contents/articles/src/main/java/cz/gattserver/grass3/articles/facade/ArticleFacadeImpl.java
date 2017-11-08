package cz.gattserver.grass3.articles.facade;

import java.time.LocalDateTime;
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

import cz.gattserver.grass3.articles.dao.ArticleRepository;
import cz.gattserver.grass3.articles.domain.Article;
import cz.gattserver.grass3.articles.domain.ArticleJSResource;
import cz.gattserver.grass3.articles.dto.ArticleDTO;
import cz.gattserver.grass3.articles.dto.ArticleDraftOverviewDTO;
import cz.gattserver.grass3.articles.editor.api.ContextImpl;
import cz.gattserver.grass3.articles.events.ArticlesProcessProgressEvent;
import cz.gattserver.grass3.articles.events.ArticlesProcessResultEvent;
import cz.gattserver.grass3.articles.events.ArticlesProcessStartEvent;
import cz.gattserver.grass3.articles.lexer.Lexer;
import cz.gattserver.grass3.articles.parser.ArticleParser;
import cz.gattserver.grass3.articles.parser.HTMLTrimmer;
import cz.gattserver.grass3.articles.parser.PluginBag;
import cz.gattserver.grass3.articles.parser.PluginRegister;
import cz.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import cz.gattserver.grass3.articles.parser.interfaces.AbstractParser;
import cz.gattserver.grass3.articles.parser.interfaces.Context;
import cz.gattserver.grass3.articles.service.impl.ArticlesContentService;
import cz.gattserver.grass3.articles.util.ArticlesMapper;
import cz.gattserver.grass3.events.EventBus;
import cz.gattserver.grass3.facades.ContentNodeFacade;
import cz.gattserver.grass3.model.domain.ContentNode;
import cz.gattserver.grass3.model.domain.ContentTag;
import cz.gattserver.grass3.model.dto.UserInfoDTO;

@Transactional
@Component
public class ArticleFacadeImpl implements ArticleFacade {

	@Autowired
	private EventBus eventBus;

	@Autowired
	private ContentNodeFacade contentNodeFacade;

	@Autowired
	private ArticlesMapper articlesMapper;

	@Autowired
	private ArticleRepository articleRepository;

	@Autowired
	private PluginRegister pluginRegister;

	private Context processArticle(String source, String contextRoot) {

		if (contextRoot == null)
			throw new IllegalArgumentException("ContextRoot nemůže být null");

		Lexer lexer = new Lexer(source);
		AbstractParser parser = new ArticleParser();
		PluginBag pluginBag = new PluginBag(lexer, contextRoot, pluginRegister);

		// výstup
		AbstractElementTree tree = parser.parse(pluginBag);
		Context ctx = new ContextImpl();
		tree.generate(ctx);

		return ctx;
	}

	/**
	 * Smaže článek
	 * 
	 * @param article
	 *            článek ke smazání
	 * @return {@code true} pokud se zdařilo smazat jiank {@code false}
	 */
	public void deleteArticle(Long id) {
		// smaž článek
		articleRepository.delete(id);

		// smaž jeho content node
		contentNodeFacade.deleteByContentId(id);
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

	/**
	 * Uloží článek
	 * 
	 * @param name
	 *            název článku
	 * @param text
	 *            obsah článku
	 * @param tags
	 *            klíčová slova článku
	 * @param publicated
	 *            je článek publikován ?
	 * @param nodeId
	 *            kategorie do které se vkládá
	 * @param authorId
	 *            uživatel, který článek vytvořil
	 * @param contextRoot
	 *            od jakého adresového kořene se mají generovat linky v článku
	 * @param processForm
	 *            jakým způsobem se má článek zpracovat
	 * @param existingId
	 *            id, jde-li o úpravu existujícího článku
	 * @return identifikátor článku pokud vše dopadlo v pořádku, jinak
	 *         {@code null}
	 */
	public Long saveArticle(String name, String text, Collection<String> tags, boolean publicated, Long nodeId,
			Long authorId, String contextRoot, ArticleProcessForm processForm, Long existingId) {
		return saveArticle(name, text, tags, publicated, nodeId, authorId, contextRoot, processForm, existingId, null,
				null);
	}

	/**
	 * Uloží článek
	 * 
	 * @param name
	 *            název článku
	 * @param text
	 *            obsah článku
	 * @param tags
	 *            klíčová slova článku
	 * @param publicated
	 *            je článek publikován ?
	 * @param nodeId
	 *            kategorie do které se vkládá
	 * @param authorId
	 *            uživatel, který článek vytvořil
	 * @param contextRoot
	 *            od jakého adresového kořene se mají generovat linky v článku
	 * @param processForm
	 *            jakým způsobem se má článek zpracovat
	 * @param existingId
	 *            id, jde-li o úpravu existujícího článku
	 * @param partNumber
	 *            číslo části, je-li editována specifická část článku (povinné,
	 *            pouze jde-li o ukládání draftu)
	 * @param draftSourceId
	 *            id existujícího zdrojového článku, jde-li o draft existujícího
	 *            článku
	 * @return identifikátor článku pokud vše dopadlo v pořádku, jinak
	 *         {@code null}
	 */
	public Long saveArticle(String name, String text, Collection<String> tags, boolean publicated, Long nodeId,
			Long authorId, String contextRoot, ArticleProcessForm processForm, Long existingId, Integer partNumber,
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
			article.setSearchableOutput(HTMLTrimmer.trim(context.getOutput()));
		}
		article.setText(text);

		// ulož ho a nasetuj jeho id
		article = articleRepository.save(article);

		if (existingId == null) {
			// vytvoř odpovídající content node
			ContentNode contentNode = contentNodeFacade.save(ArticlesContentService.ID, article.getId(), name, tags,
					publicated, nodeId, authorId, draft, null, draftSourceId);

			// ulož do článku referenci na jeho contentnode
			article.setContentNode(contentNode);
			articleRepository.save(article);
		} else {
			contentNodeFacade.modify(article.getContentNode().getId(), name, tags, publicated);
		}

		return article.getId();
	}

	/**
	 * Získá článek dle jeho identifikátoru pro jeho celé zobrazení
	 * 
	 * @param id
	 *            identifikátor
	 * @return DTO článku
	 */
	public ArticleDTO getArticleForDetail(Long id) {
		Article article = articleRepository.findOne(id);
		if (article == null)
			return null;
		ArticleDTO articleDTO = articlesMapper.mapArticleForDetail(article);
		return articleDTO;
	}

	/**
	 * Spustí přegenerování
	 */
	@Async
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

			ArticleProcessForm articleProcessForm = Boolean.TRUE.equals(article.getContentNode().getDraft())
					? ArticleProcessForm.PREVIEW : ArticleProcessForm.FULL;
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

	/**
	 * Získá všechny články a namapuje je pro použití při vyhledávání
	 * 
	 * @return
	 */
	public List<ArticleDTO> getAllArticlesForSearch() {
		List<Article> articles = articleRepository.findAll();
		if (articles == null)
			return null;
		List<ArticleDTO> articleDTOs = articlesMapper.mapArticlesForSearch(articles);
		return articleDTOs;
	}

	@Override
	public List<ArticleDraftOverviewDTO> getDraftsForUser(UserInfoDTO user) {
		List<Article> articles = articleRepository.findDraftsForUser(user.getId(), user.isAdmin());
		if (articles == null)
			return null;
		List<ArticleDraftOverviewDTO> articleDTOs = articlesMapper.mapArticlesForDraftOverview(articles);
		return articleDTOs;
	}

}
