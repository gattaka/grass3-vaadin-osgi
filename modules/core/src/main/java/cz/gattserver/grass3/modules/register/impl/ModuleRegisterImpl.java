package cz.gattserver.grass3.modules.register.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.modules.ContentService;
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
	 * Ošetření null kolekcí
	 */
	@PostConstruct
	private void init() {

		if (contentServices == null)
			contentServices = new ArrayList<ContentService>();

		if (sectionServices == null) {
			sectionServices = new ArrayList<SectionService>();
		}
	}

	/**
	 * Obsahy
	 */
	@Autowired(required = false)
	private List<ContentService> contentServices;

	public List<ContentService> getContentServices() {
		return contentServices;
	}

	public ContentService getContentServiceByName(String contentReaderID) {
		if (contentServices == null)
			return null;
		for (ContentService contentService : contentServices) {
			if (contentService.getContentID().equals(contentReaderID))
				return contentService;
		}
		return null;
	}

	/**
	 * Sekce
	 */
	@Autowired(required = false)
	private List<SectionService> sectionServices;

	public List<SectionService> getSectionServices() {
		return sectionServices;
	}

}
