package cz.gattserver.grass3;

import java.util.List;

import cz.gattserver.grass3.service.IContentService;
import cz.gattserver.grass3.service.ISectionService;

/**
 * {@link IServiceHolder} udržuje přehled všech přihlášených modulů. Zároveň
 * přijímá registrace listenerů vůči bind a unbind metodám pro jednotlivé
 * služby.
 * 
 * @author gatt
 * 
 */
public interface IServiceHolder {

	public List<IContentService> getContentServices();

	public IContentService getContentServiceByName(String contentReaderID);

	public List<ISectionService> getSectionServices();

}
