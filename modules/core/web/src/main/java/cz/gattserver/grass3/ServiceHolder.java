package cz.gattserver.grass3;

import java.util.List;

import cz.gattserver.grass3.service.ContentService;
import cz.gattserver.grass3.service.SectionService;

/**
 * {@link ServiceHolder} udržuje přehled všech přihlášených modulů. Zároveň
 * přijímá registrace listenerů vůči bind a unbind metodám pro jednotlivé
 * služby.
 * 
 * @author gatt
 * 
 */
public interface ServiceHolder {

	public List<ContentService> getContentServices();

	public ContentService getContentServiceByName(String contentReaderID);

	public List<SectionService> getSectionServices();

}
