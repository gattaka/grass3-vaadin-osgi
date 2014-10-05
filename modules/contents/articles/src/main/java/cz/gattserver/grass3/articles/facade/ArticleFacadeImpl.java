package cz.gattserver.grass3.articles.facade;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass3.articles.dao.ArticleRepository;
import cz.gattserver.grass3.articles.domain.Article;
import cz.gattserver.grass3.articles.domain.ArticleJSResource;
import cz.gattserver.grass3.articles.dto.ArticleDTO;
import cz.gattserver.grass3.articles.editor.api.ContextImpl;
import cz.gattserver.grass3.articles.lexer.Lexer;
import cz.gattserver.grass3.articles.parser.ArticleParser;
import cz.gattserver.grass3.articles.parser.HTMLTrimmer;
import cz.gattserver.grass3.articles.parser.PluginBag;
import cz.gattserver.grass3.articles.parser.PluginRegister;
import cz.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import cz.gattserver.grass3.articles.parser.interfaces.AbstractParser;
import cz.gattserver.grass3.articles.parser.interfaces.IContext;
import cz.gattserver.grass3.articles.service.impl.ArticlesContentService;
import cz.gattserver.grass3.articles.util.ArticlesMapper;
import cz.gattserver.grass3.facades.IContentNodeFacade;
import cz.gattserver.grass3.model.dao.ContentNodeRepository;
import cz.gattserver.grass3.model.domain.ContentNode;
import cz.gattserver.grass3.model.dto.ContentNodeDTO;
import cz.gattserver.grass3.model.dto.NodeDTO;
import cz.gattserver.grass3.model.dto.UserInfoDTO;

@Transactional
@Component("articleFacade")
public class ArticleFacadeImpl implements IArticleFacade {

	@Resource(name = "contentNodeFacade")
	private IContentNodeFacade contentNodeFacade;

	@Resource(name = "articlesMapper")
	private ArticlesMapper articlesMapper;

	@Autowired
	private ContentNodeRepository contentNodeRepository;

	@Autowired
	private ArticleRepository articleRepository;

	@Resource(name = "pluginRegister")
	private PluginRegister pluginRegister;

	private IContext processArticle(String source, String contextRoot) {

		if (contextRoot == null)
			throw new IllegalArgumentException("ContextRoot nemůže být null");

		Lexer lexer = new Lexer(source);
		AbstractParser parser = new ArticleParser();
		PluginBag pluginBag = new PluginBag(lexer, contextRoot, pluginRegister);

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
	public boolean saveTemp(String name, String text, String tags, NodeDTO category, UserInfoDTO author) {
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
		articleRepository.delete(articleDTO.getId());

		// smaž jeho content node
		ContentNodeDTO contentNodeDTO = articleDTO.getContentNode();
		if (contentNodeDTO != null) {
			if (contentNodeFacade.delete(contentNodeDTO.getId()) == false)
				return false;
		}

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
	public boolean modifyArticle(String name, String text, Collection<String> tags, boolean publicated,
			ArticleDTO articleDTO, String contextRoot) {

		// článek
		Article article = articleRepository.findOne(articleDTO.getId());

		// nasetuj do něj vše potřebné
		IContext context = processArticle(text, contextRoot);
		article.setOutputHTML(context.getOutput());
		article.setPluginCSSResources(context.getCSSResources());

		article.setPluginJSResources(createJSResourcesSet(context.getJSResources()));

		article.setText(text);
		article.setSearchableOutput(HTMLTrimmer.trim(context.getOutput()));

		// ulož ho
		if (articleRepository.save(article) == null)
			return false;

		// content node
		if (contentNodeFacade.modify(articleDTO.getContentNode().getId(), name, tags, publicated) == false)
			return false;

		return true;
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
	 * @param category
	 *            kategorie do kteér se vkládá
	 * @param author
	 *            uživatel, který článek vytvořil
	 * @return identifikátor článku pokud vše dopadlo v pořádku, jinak
	 *         {@code null}
	 */
	public Long saveArticle(String name, String text, Collection<String> tags, boolean publicated, NodeDTO category,
			UserInfoDTO author, String contextRoot) {

		// vytvoř nový článek
		Article article = new Article();

		// nasetuj do něj vše potřebné
		IContext context = processArticle(text, contextRoot);
		article.setOutputHTML(context.getOutput());
		article.setPluginCSSResources(context.getCSSResources());
		article.setPluginJSResources(createJSResourcesSet(context.getJSResources()));
		article.setText(text);
		article.setSearchableOutput(HTMLTrimmer.trim(context.getOutput()));

		// ulož ho a nasetuj jeho id
		article = articleRepository.save(article);
		
		// vytvoř odpovídající content node
		ContentNode contentNode = contentNodeFacade.save(ArticlesContentService.ID, article.getId(), name, tags,
				publicated, category.getId(), author.getId());

		// ulož do článku referenci na jeho contentnode
		article.setContentNode(contentNode);
		articleRepository.save(article);

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
	 * Získá všechny články pro přegenerování
	 */
	public List<ArticleDTO> getAllArticlesForReprocess() {
		List<Article> articles = articleRepository.findAll();
		if (articles == null)
			return null;
		List<ArticleDTO> articleDTOs = articlesMapper.mapArticlesForReprocess(articles);
		return articleDTOs;
	}

	/**
	 * Získá všechny články pro přehled
	 * 
	 * @return
	 */
	public List<ArticleDTO> getAllArticlesForOverview() {
		List<Article> articles = articleRepository.findAll();
		if (articles == null)
			return null;
		List<ArticleDTO> articleDTOs = articlesMapper.mapArticlesForOverview(articles);
		return articleDTOs;
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

}
