package org.myftp.gattserver.grass3.ui.util.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.myftp.gattserver.grass3.pages.factories.template.IPageFactory;
import org.myftp.gattserver.grass3.ui.util.IPageFactoriesRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value = "pageFactoriesRegister")
public class PageFactoriesRegisterImpl implements IPageFactoriesRegister {

	/**
	 * Domovská stránka
	 */
	@Resource(name = "homePageFactory")
	private IPageFactory homePageFactory;

	/**
	 * Hlavní mapa stránek
	 */
	@Autowired
	private List<IPageFactory> pageFactories;

	private Map<String, IPageFactory> factories = new HashMap<String, IPageFactory>();

	@PostConstruct
	public void init() {
		for (IPageFactory factory : pageFactories)
			factories.put(factory.getPageName(), factory);
	}

	public void setHomepageFactory(IPageFactory homepageFactory) {
		this.homePageFactory = homepageFactory;
	}

	/**
	 * Dělá prakticky to samé jako původní get, až na to, že pakliže není
	 * nalezena factory pro daný klíč, je vrácena factory homepage
	 */
	public IPageFactory get(String key) {
		IPageFactory factory = factories.get(key);
		return factory == null ? homePageFactory : factory;
	}

	/**
	 * Původní put metoda - má prakticky jediné použití a tím je tvorba aliasů
	 */
	public IPageFactory putAlias(String pageName, IPageFactory factory) {
		return factories.put(pageName, factory);
	}

}
