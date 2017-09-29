package cz.gattserver.grass3;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.service.ContentService;
import cz.gattserver.grass3.service.SectionService;

/**
 * {@link ServiceHolderImpl} udržuje přehled všech přihlášených modulů. Zároveň
 * přijímá registrace listenerů vůči bind a unbind metodám pro jednotlivé
 * služby.
 * 
 * @author gatt
 * 
 */
@Component
public class ServiceHolderImpl implements ServiceHolder {

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
