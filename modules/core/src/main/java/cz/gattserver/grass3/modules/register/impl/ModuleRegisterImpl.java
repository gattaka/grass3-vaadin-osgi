package cz.gattserver.grass3.modules.register.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.modules.ContentModule;
import cz.gattserver.grass3.modules.SectionService;
import cz.gattserver.grass3.modules.register.ModuleRegister;

/**
 * {@link ModuleRegisterImpl} udržuje přehled všech přihlášených modulů. Zároveň
 * přijímá registrace listenerů vůči bind a unbind metodám pro jednotlivé
 * služby.
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
	private List<ContentModule> contentServices;

	/**
	 * Sekce
	 */
	@Autowired(required = false)
	private List<SectionService> sectionServices;

	/**
	 * Ošetření null kolekcí
	 */
	@PostConstruct
	private void init() {
		if (contentServices == null)
			contentServices = new ArrayList<ContentModule>();

		if (sectionServices == null) {
			sectionServices = new ArrayList<SectionService>();
		}
	}

	@Override
	public List<ContentModule> getContentServices() {
		return contentServices;
	}

	@Override
	public ContentModule getContentServiceByName(String contentReaderID) {
		if (contentServices == null)
			return null;
		for (ContentModule contentService : contentServices) {
			if (contentService.getContentID().equals(contentReaderID))
				return contentService;
		}
		return null;
	}

	@Override
	public List<SectionService> getSectionServices() {
		return sectionServices;
	}

}
