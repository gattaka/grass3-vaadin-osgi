package cz.gattserver.grass3.modules.register.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.common.util.CZComparator;
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

	private static Logger logger = LoggerFactory.getLogger(ModuleRegisterImpl.class);

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
	private final void init() {
		logger.info("ModuleRegister init");

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
	public List<ContentModule> getContentModules() {
		return Collections.unmodifiableList(injectedContentModules);
	}

	@Override
	public ContentModule getContentModulesByName(String contentReaderID) {
		return contentModules.get(contentReaderID);
	}

	@Override
	public List<SectionService> getSectionServices() {
		injectedSectionModules.sort((s1, s2) -> {
			try {
				return new CZComparator().compare(s1.getSectionCaption(), s2.getSectionCaption());
			} catch (ParseException e) {
				logger.error("Nezdařilo se seřadit sekce", e);
				throw new RuntimeException(e);
			}
		});
		return Collections.unmodifiableList(injectedSectionModules);
	}

}
