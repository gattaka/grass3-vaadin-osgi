package cz.gattserver.grass3.modules.register.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.modules.ContentModule;
import cz.gattserver.grass3.modules.SectionService;
import cz.gattserver.grass3.modules.register.ModuleRegister;

/**
 * {@link ModuleRegisterImpl} udržuje přehled všech přihlášených modulů.
 * 
 * @author gatt
 * 
 */
@Component
public class ModuleRegisterImpl implements ModuleRegister {

	/**
	 * Obsahy
	 */
	@Autowired(required = false)
	private List<ContentModule> injectedContentModules;
	private Map<String, ContentModule> contentModules;

	/**
	 * Sekce
	 */
	@Autowired(required = false)
	private List<SectionService> injectedSectionModules;

	@PostConstruct
	private void init() {
		// Ošetření null kolekcí
		if (injectedContentModules == null)
			injectedContentModules = new ArrayList<>();
		if (injectedSectionModules == null) {
			injectedSectionModules = new ArrayList<>();
		}

		contentModules = new HashMap<>();
		for (ContentModule c : injectedContentModules)
			contentModules.put(c.getContentID(), c);
	}

	@Override
	public List<ContentModule> getContentServices() {
		return Collections.unmodifiableList(injectedContentModules);
	}

	@Override
	public ContentModule getContentServiceByName(String contentReaderID) {
		return contentModules.get(contentReaderID);
	}

	@Override
	public List<SectionService> getSectionServices() {
		return Collections.unmodifiableList(injectedSectionModules);
	}

}
