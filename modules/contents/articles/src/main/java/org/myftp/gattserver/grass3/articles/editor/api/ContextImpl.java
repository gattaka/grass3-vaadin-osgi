package org.myftp.gattserver.grass3.articles.editor.api;

import java.util.LinkedHashSet;
import java.util.Set;

import org.myftp.gattserver.grass3.articles.parser.interfaces.IContext;


/**
 * Kontext pouzity pri generovani kodu.
 */
public class ContextImpl implements IContext {

	/**
	 * Vystupni stream.
	 */
	private StringBuilder out;

	/**
	 * úroveň text z pohledu nadpisů 0 .. nenastaveno - výchozí
	 */
	private int textLevel = 0;
	
	/**
	 * Pořadové číslo nadpisu - aby se dal obsah upravovat po částech
	 */
	private int headerIdentifier = 0;

	/**
	 * Dodatečné zdroj vyžadované pluginy
	 */
	private Set<String> cssResources = new LinkedHashSet<String>();
	private Set<String> jsResources = new LinkedHashSet<String>();

	public ContextImpl() {
		this.out = new StringBuilder();
	}

	public void print(String s) {
		out.append(s);
	}

	public void println(String s) {
		out.append(s);
	}

	public void setHeaderLevel(int level) {

		// ani větší než 5
		if (level > 5) {
			level = 5;
		}

		// pokud byla již úroveň změněná, nejprve
		// uzavři předchozí odsazovací div
		if (this.textLevel != 0) {
			out.append("</div>");
		}

		// ulož si pro příští porovnání aktuální level
		textLevel = level;

		// vlož odsazovací div
		out.append("<div class=\"level").append(level).append("\">");
	}

	public String getOutput() {
		/**
		 * pokud byla úroveň odsazení změněná, uzavři odsazovací div
		 * 
		 * Zde by se mohl sice textLevel rovnou vynulovat, ale já připouštím
		 * možnost získat výstup a pak pokračovat ve vypisování výstupu
		 * 
		 */
		return (this.textLevel != 0) ? (out.toString() + "</div>") : out.toString();
	}

	public int getNextHeaderIdentifier() {
		return headerIdentifier++;
	}
	
	public void resetHeaderLevel() {

		// pokud byla úroveň opravdu změněná,
		// uzavři předchozí odsazovací div
		if (this.textLevel != 0) {
			out.append("</div>");
			textLevel = 0;
		}

	}

	public void addCSSResource(String url) {
		cssResources.add(url);
	}

	public void addJSResource(String url) {
		jsResources.add(url);
	}

	public Set<String> getCSSResources() {
		return cssResources;
	}

	public Set<String> getJSResources() {
		return jsResources;
	}
}
