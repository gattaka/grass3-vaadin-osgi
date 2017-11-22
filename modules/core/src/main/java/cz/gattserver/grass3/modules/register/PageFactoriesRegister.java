package cz.gattserver.grass3.modules.register;

import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;

public interface PageFactoriesRegister {

	public void setHomepageFactory(PageFactory homepageFactory);

	/**
	 * Dělá prakticky to samé jako původní get, až na to, že pakliže není
	 * nalezena factory pro daný klíč, je vrácena factory homepage
	 */
	public PageFactory get(String key);

	/**
	 * Původní put metoda - má prakticky jediné použití a tím je tvorba aliasů
	 */
	public PageFactory putAlias(String pageName, PageFactory factory);

}
