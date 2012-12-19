package org.myftp.gattserver.grass3.articles.facade;

import org.myftp.gattserver.grass3.articles.dao.ArticleDAO;
import org.myftp.gattserver.grass3.articles.domain.Article;
import org.myftp.gattserver.grass3.articles.dto.ArticleDTO;
import org.myftp.gattserver.grass3.articles.editor.api.ContextImpl;
import org.myftp.gattserver.grass3.articles.lexer.Lexer;
import org.myftp.gattserver.grass3.articles.parser.ArticleParser;
import org.myftp.gattserver.grass3.articles.parser.PluginBag;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractParser;
import org.myftp.gattserver.grass3.articles.parser.interfaces.IContext;
import org.myftp.gattserver.grass3.articles.service.impl.ArticlesContentService;
import org.myftp.gattserver.grass3.articles.util.ArticlesMapper;
import org.myftp.gattserver.grass3.facades.ContentNodeFacade;
import org.myftp.gattserver.grass3.model.dao.ContentNodeDAO;
import org.myftp.gattserver.grass3.model.domain.ContentNode;
import org.myftp.gattserver.grass3.model.dto.ContentNodeDTO;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;

public enum ArticleFacade {

	INSTANCE;

	private ContentNodeFacade contentNodeFacade = ContentNodeFacade.INSTANCE;
	private ArticlesMapper articlesMapper = ArticlesMapper.INSTANCE;

	private IContext processArticle(String source) {

		Lexer lexer = new Lexer(source);
		AbstractParser parser = new ArticleParser();
		PluginBag pluginBag = new PluginBag(lexer, null);

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
	public ArticleDTO processPreview(String text) {

		IContext context = processArticle(text);

		ArticleDTO articleDTO = new ArticleDTO();
		articleDTO.setPluginCSSResources(context.getCSSResources());
		articleDTO.setPluginJSResources(context.getJSResources());
		articleDTO.setOutputHTML(context.getOutput());

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
	 * Upraví článek
	 * 
	 * @param name
	 *            název článku
	 * @param text
	 *            obsah článku
	 * @param tags
	 *            klíčová slova článku
	 * @param articleDTO
	 *            původní článek
	 * @return {@code true} pokud se úprava zdařila, jinak {@code false}
	 */
	public boolean modifyArticle(String name, String text, String tags,
			ArticleDTO articleDTO) {

		// článek
		ArticleDAO articleDAO = new ArticleDAO();
		Article article = articleDAO.findByID(articleDTO.getId());

		// nasetuj do něj vše potřebné
		IContext context = processArticle(text);
		article.setOutputHTML(context.getOutput());
		article.setPluginCSSResources(context.getCSSResources());
		article.setPluginJSResources(context.getJSResources());
		article.setText(text);

		// ulož ho
		if (articleDAO.merge(article) == false)
			return false;

		// content node
		if (contentNodeFacade.modify(articleDTO.getContentNode(), name, tags) == false)
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
	 * @param category
	 *            kategorie do kteér se vkládá
	 * @param author
	 *            uživatel, který článek vytvořil
	 * @return identifikátor článku pokud vše dopadlo v pořádku, jinak
	 *         {@code null}
	 */
	public Long saveArticle(String name, String text, String tags,
			NodeDTO category, UserInfoDTO author) {

		// vytvoř nový článek
		ArticleDAO articleDAO = new ArticleDAO();
		Article article = new Article();

		// nasetuj do něj vše potřebné
		IContext context = processArticle(text);
		article.setOutputHTML(context.getOutput());
		article.setPluginCSSResources(context.getCSSResources());
		article.setPluginJSResources(context.getJSResources());
		article.setText(text);

		// ulož ho a nasetuj jeho id
		Long contentId = (Long) articleDAO.save(article);
		if (contentId == null)
			return null;
		article.setId(contentId);

		// vytvoř odpovídající content node
		ContentNodeDTO contentNodeDTO = contentNodeFacade.save(
				ArticlesContentService.ID, contentId, name, tags, category,
				author);

		if (contentNodeDTO == null)
			return null;

		// ulož do článku referenci na jeho contentnode
		ContentNodeDAO contentNodeDAO = new ContentNodeDAO();
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

		ArticleDAO dao = new ArticleDAO();
		Article article = dao.findByID(id);

		if (article == null)
			return null;

		ArticleDTO articleDTO = articlesMapper.map(article);

		dao.closeSession();

		return articleDTO;
	}

}
