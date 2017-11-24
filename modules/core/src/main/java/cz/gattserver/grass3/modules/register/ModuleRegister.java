package cz.gattserver.grass3.modules.register;

import java.util.List;

import cz.gattserver.grass3.modules.ContentModule;
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

	List<ContentModule> getContentServices();

	ContentModule getContentServiceByName(String contentReaderID);

	List<SectionService> getSectionServices();

}
