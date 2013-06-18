package org.myftp.gattserver.grass3;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.myftp.gattserver.grass3.service.IContentService;
import org.myftp.gattserver.grass3.service.ISectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * {@link ServiceHolderImpl} udržuje přehled všech přihlášených modulů. Zároveň
 * přijímá registrace listenerů vůči bind a unbind metodám pro jednotlivé
 * služby.
 * 
 * @author gatt
 * 
 */
@Component("serviceHolder")
public class ServiceHolderImpl implements IServiceHolder {

	/**
	 * Ošetření null kolekcí
	 */
	@PostConstruct
	private void init() {

		if (contentServices == null)
			contentServices = new ArrayList<IContentService>();

		if (sectionServices == null) {
			sectionServices = new ArrayList<ISectionService>();
		}
	}

	/**
	 * Obsahy
	 */
	@Autowired(required = false)
	private List<IContentService> contentServices;

	public List<IContentService> getContentServices() {
		return contentServices;
	}

	public IContentService getContentServiceByName(String contentReaderID) {
		if (contentServices == null)
			return null;
		for (IContentService contentService : contentServices) {
			if (contentService.getContentID().equals(contentReaderID))
				return contentService;
		}
		return null;
	}

	/**
	 * Sekce
	 */
	@Autowired(required = false)
	private List<ISectionService> sectionServices;

	public List<ISectionService> getSectionServices() {
		return sectionServices;
	}

}
