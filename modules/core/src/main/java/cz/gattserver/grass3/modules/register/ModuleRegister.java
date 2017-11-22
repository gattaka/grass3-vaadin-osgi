package cz.gattserver.grass3.modules.register;

import java.util.List;

import cz.gattserver.grass3.modules.ContentService;
import cz.gattserver.grass3.modules.SectionService;

/**
 * {@link ModuleRegister} udržuje přehled všech přihlášených modulů. Zároveň
 * přijímá registrace listenerů vůči bind a unbind metodám pro jednotlivé
 * služby.
 * 
 * @author gatt
 * 
 */
public interface ModuleRegister {

	public List<ContentService> getContentServices();

	public ContentService getContentServiceByName(String contentReaderID);

	public List<SectionService> getSectionServices();

}
