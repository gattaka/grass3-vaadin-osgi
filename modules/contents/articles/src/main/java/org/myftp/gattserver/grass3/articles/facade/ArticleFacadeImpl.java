package org.myftp.gattserver.grass3.articles.facade;

import java.util.List;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.articles.dao.ArticleDAO;
import org.myftp.gattserver.grass3.articles.domain.Article;
import org.myftp.gattserver.grass3.articles.dto.ArticleDTO;
import org.myftp.gattserver.grass3.articles.editor.api.ContextImpl;
import org.myftp.gattserver.grass3.articles.lexer.Lexer;
import org.myftp.gattserver.grass3.articles.parser.ArticleParser;
import org.myftp.gattserver.grass3.articles.parser.HTMLTrimmer;
import org.myftp.gattserver.grass3.articles.parser.PluginBag;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractParser;
import org.myftp.gattserver.grass3.articles.parser.interfaces.IContext;
import org.myftp.gattserver.grass3.articles.service.impl.ArticlesContentService;
import org.myftp.gattserver.grass3.articles.util.ArticlesMapper;
import org.myftp.gattserver.grass3.facades.IContentNodeFacade;
import org.myftp.gattserver.grass3.model.dao.ContentNodeDAO;
import org.myftp.gattserver.grass3.model.domain.ContentNode;
import org.myftp.gattserver.grass3.model.dto.ContentNodeDTO;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component("articleFacade")
public class ArticleFacadeImpl implements IArticleFacade,
		ApplicationContextAware {

	@Resource(name = "contentNodeFacade")
	private IContentNodeFacade contentNodeFacade;

	@Resource(name = "articlesMapper")
	private ArticlesMapper articlesMapper;

	@Resource(name = "contentNodeDAO")
	private ContentNodeDAO contentNodeDAO;

	@Resource(name = "articleDAO")
	private ArticleDAO articleDAO;

	private ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	private IContext processArticle(String source, String contextRoot) {

		if (contextRoot == null)
			throw new IllegalArgumentException("ContextRoot nemůže být null");

		Lexer lexer = new Lexer(source);
		AbstractParser parser = new ArticleParser();
		PluginBag pluginBag = (PluginBag) applicationContext.getBean(
				"pluginBag", lexer, contextRoot);

		// výstup
		AbstractElementTree tree = parser.parse(pluginBag);
		IContext ctx = new ContextImpl();
		tree.generate(ctx);

		return ctx;
	}

	/**
	 * Zpracuje článek a vrátí jeho HMTL výstup.
	 * 
	 * @param text
	 *            vstupní text článku
	 * @return výstupní DTO článku, pokud se překlad zdařil, jinak {@code null}
	 */
	public ArticleDTO processPreview(String text, String contextRoot) {

		IContext context = processArticle(text, contextRoot);

		ArticleDTO articleDTO = new ArticleDTO();
		articleDTO.setPluginCSSResources(context.getCSSResources());
		articleDTO.setPluginJSResources(context.getJSResources());
		articleDTO.setOutputHTML(context.getOutput());
		articleDTO.setSearchableOutput(HTMLTrimmer.trim(context.getOutput()));

		return articleDTO;

	}

	/**
	 * Uloží rozpracovaný článek - nepřekládá ho, jenom uloží obsah polí v
	 * editoru
	 * 
	 * @param name
	 *            název článku
	 * @param text
	 *            obsah článku
	 * @param tags
	 *            klíčová slova článku
	 * @param category
	 *            kategorie do kteér se vkládá
	 * @param author
	 *            uživatel, který článek vytvořil
	 * @return {@code true} pokud vše dopadlo v pořádku, jinak {@code false}
	 */
	public boolean saveTemp(String name, String text, String tags,
			NodeDTO category, UserInfoDTO author) {
		// TODO
		return true;
	}

	/**
	 * Smaže článek
	 * 
	 * @param article
	 *            článek ke smazání
	 * @return {@code true} pokud se zdařilo smazat jiank {@code false}
	 */
	public boolean deleteArticle(ArticleDTO articleDTO) {

		// smaž článek
		if (articleDAO.delete(articleDTO.getId()) == false)
			return false;

		// smaž jeho content node
		ContentNodeDTO contentNodeDTO = articleDTO.getContentNode();
		if (contentNodeFacade.delete(contentNodeDTO) == false)
			return false;

		return true;
	}

	/**
	 * Upraví článek
	 * 
	 * @param name
	 *            název článku
	 * @param text
	 *            obsah článku
	 * @param tags
	 *            klíčová slova článku
	 * @param publicated
	 *            je článek publikován ?
	 * @param articleDTO
	 *            původní článek
	 * @return {@code true} pokud se úprava zdařila, jinak {@code false}
	 */
	public boolean modifyArticle(String name, String text, String tags,
			boolean publicated, ArticleDTO articleDTO, String contextRoot) {

		// článek
		Article article = articleDAO.findByID(articleDTO.getId());
		articleDAO.closeSession();

		// nasetuj do něj vše potřebné
		IContext context = processArticle(text, contextRoot);
		article.setOutputHTML(context.getOutput());
		article.setPluginCSSResources(context.getCSSResources());
		article.setPluginJSResources(context.getJSResources());
		article.setText(text);
		article.setSearchableOutput(HTMLTrimmer.trim(context.getOutput()));

		// ulož ho
		if (articleDAO.merge(article) == false)
			return false;

		// content node
		if (contentNodeFacade.modify(articleDTO.getContentNode(), name, tags,
				publicated) == false)
			return false;

		return true;
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
	 * @param category
	 *            kategorie do kteér se vkládá
	 * @param author
	 *            uživatel, který článek vytvořil
	 * @return identifikátor článku pokud vše dopadlo v pořádku, jinak
	 *         {@code null}
	 */
	public Long saveArticle(String name, String text, String tags,
			boolean publicated, NodeDTO category, UserInfoDTO author,
			String contextRoot) {

		// vytvoř nový článek
		Article article = new Article();

		// nasetuj do něj vše potřebné
		IContext context = processArticle(text, contextRoot);
		article.setOutputHTML(context.getOutput());
		article.setPluginCSSResources(context.getCSSResources());
		article.setPluginJSResources(context.getJSResources());
		article.setText(text);
		article.setSearchableOutput(HTMLTrimmer.trim(context.getOutput()));

		// ulož ho a nasetuj jeho id
		Long contentId = (Long) articleDAO.save(article);
		if (contentId == null)
			return null;
		article.setId(contentId);

		// vytvoř odpovídající content node
		ContentNodeDTO contentNodeDTO = contentNodeFacade.save(
				ArticlesContentService.ID, contentId, name, tags, publicated,
				category, author);

		if (contentNodeDTO == null)
			return null;

		// ulož do článku referenci na jeho contentnode
		ContentNode contentNode = contentNodeDAO.findByID(contentNodeDTO
				.getId());
		contentNodeDAO.closeSession();

		article.setContentNode(contentNode);
		if (articleDAO.merge(article) == false)
			return null;

		return article.getId();
	}

	/**
	 * Získá článek dle jeho identifikátoru
	 * 
	 * @param id
	 *            identifikátor
	 * @return DTO článku
	 */
	public ArticleDTO getArticleById(Long id) {

		Article article = articleDAO.findByID(id);

		if (article == null)
			return null;

		ArticleDTO articleDTO = articlesMapper.map(article);

		articleDAO.closeSession();

		return articleDTO;
	}

	/**
	 * Získá všechny články
	 * 
	 * @return
	 */
	public List<ArticleDTO> getAllArticles() {

		List<Article> articles = articleDAO.findAll();

		if (articles == null)
			return null;

		List<ArticleDTO> articleDTOs = articlesMapper.map(articles);

		articleDAO.closeSession();

		return articleDTOs;

	}

}
