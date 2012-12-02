package org.myftp.gattserver.grass3.articles.facade;

import org.myftp.gattserver.grass3.articles.dto.ArticleDTO;
import org.myftp.gattserver.grass3.articles.editor.api.ContextImpl;
import org.myftp.gattserver.grass3.articles.lexer.Lexer;
import org.myftp.gattserver.grass3.articles.parser.ArticleParser;
import org.myftp.gattserver.grass3.articles.parser.PluginBag;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractParser;
import org.myftp.gattserver.grass3.articles.parser.interfaces.IContext;

public enum ArticleFacade {

	INSTANCE;
	
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
	 * @return {@code true} pokud vše dopadlo v pořádku, jinak {@code false}
	 */
	public boolean saveTemp(String name, String text, String tags) {
		// TODO
		return true;
	}

}
