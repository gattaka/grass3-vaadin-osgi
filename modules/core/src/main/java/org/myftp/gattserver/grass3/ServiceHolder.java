package org.myftp.gattserver.grass3;

import java.util.ArrayList;
import java.util.List;

import org.myftp.gattserver.grass3.service.IContentService;
import org.myftp.gattserver.grass3.service.ISectionService;
import org.myftp.gattserver.grass3.service.ISettingsService;

/**
 * {@link ServiceHolder} udržuje přehled všech přihlášených modulů. Zároveň
 * přijímá registrace listenerů vůči bind a unbind metodám pro jednotlivé
 * služby.
 * 
 * @author gatt
 * 
 */
public class ServiceHolder {

	// static class
	private ServiceHolder() {
	};

	/**
	 * Obsahy
	 */
	private static List<IContentService> contentServices = new ArrayList<IContentService>();

	public static boolean addContentService(IContentService contentService) {
		return contentServices.add(contentService);
	}

	public static List<IContentService> getContentServices() {
		return contentServices;
	}

	public static IContentService getContentServiceByName(String contentReaderID) {
		for (IContentService contentService : contentServices) {
			if (contentService.getContentID().equals(contentReaderID))
				return contentService;
		}
		return null;
	}

	/**
	 * Sekce
	 */
	private static List<ISectionService> sectionServices = new ArrayList<ISectionService>();

	public static boolean addSectionServices(ISectionService sectionService) {
		return sectionServices.add(sectionService);
	}

	public static List<ISectionService> getSectionServices() {
		return sectionServices;
	}

	/**
	 * Settings
	 */
	private static List<ISettingsService> settingsServices = new ArrayList<ISettingsService>();

	public static boolean addSettingsService(ISettingsService settingsService) {
		return settingsServices.add(settingsService);
	}

	public static List<ISettingsService> getSettingsServices() {
		return settingsServices;
	}

}
