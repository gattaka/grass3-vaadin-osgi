package org.myftp.gattserver.grass3.util;

import org.myftp.gattserver.grass3.pages.factories.template.IPageFactory;

public interface IPageFactoriesRegister {

	public void setHomepageFactory(IPageFactory homepageFactory);

	/**
	 * Dělá prakticky to samé jako původní get, až na to, že pakliže není
	 * nalezena factory pro daný klíč, je vrácena factory homepage
	 */
	public IPageFactory get(String key);

	/**
	 * Původní put metoda - má prakticky jediné použití a tím je tvorba aliasů
	 */
	public IPageFactory putAlias(String pageName, IPageFactory factory);

}
