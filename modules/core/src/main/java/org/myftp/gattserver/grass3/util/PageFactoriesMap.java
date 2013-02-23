package org.myftp.gattserver.grass3.util;

import java.util.HashMap;

import org.myftp.gattserver.grass3.windows.template.IPageFactory;

public class PageFactoriesMap extends HashMap<String, IPageFactory> {

	private static final long serialVersionUID = 2552200530580643096L;
	private IPageFactory homepageFactory;

	public PageFactoriesMap(IPageFactory homepageFactory) {
		this.homepageFactory = homepageFactory;
	}

	/**
	 * Dělá prakticky to samé jako původní get, až na to, že pakliže není
	 * nalezena factory pro daný klíč, je vrácena factory homepage
	 */
	@Override
	public IPageFactory get(Object key) {
		IPageFactory factory = super.get(key);
		return factory == null ? homepageFactory : factory;
	}

	/**
	 * Základní vkládácí metoda - zaregistruje factory pod jménem stránky,
	 * kterou factory vytváří
	 */
	public IPageFactory put(IPageFactory factory) {
		return super.put(factory.getPageName(), factory);
	}

	/**
	 * Původní put metoda - má prakticky jediné použití a tím je tvorba aliasů
	 */
	@Override
	public IPageFactory put(String pageName, IPageFactory factory) {
		return super.put(pageName, factory);
	}

}
