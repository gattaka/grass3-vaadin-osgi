package org.myftp.gattserver.grass3.util;

import java.util.HashMap;

import org.myftp.gattserver.grass3.windows.ifces.PageFactory;

public class PageFactoriesMap extends HashMap<String, PageFactory> {

	private static final long serialVersionUID = 2552200530580643096L;
	private PageFactory homepageFactory;

	public PageFactoriesMap(PageFactory homepageFactory) {
		this.homepageFactory = homepageFactory;
	}

	/**
	 * Dělá prakticky to samé jako původní get, až na to, že pakliže není
	 * nalezena factory pro daný klíč, je vrácena factory homepage
	 */
	@Override
	public PageFactory get(Object key) {
		PageFactory factory = super.get(key);
		return factory == null ? homepageFactory : factory;
	}

	/**
	 * Základní vkládácí metoda - zaregistruje factory pod jménem stránky,
	 * kterou factory vytváří
	 */
	public PageFactory put(PageFactory factory) {
		return super.put(factory.getPageName(), factory);
	}

	/**
	 * Původní put metoda - má prakticky jediné použití a tím je tvorba aliasů
	 */
	@Override
	public PageFactory put(String pageName, PageFactory factory) {
		return super.put(pageName, factory);
	}

}
