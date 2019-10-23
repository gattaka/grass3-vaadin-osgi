package cz.gattserver.grass3.hw.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.querydsl.core.types.OrderSpecifier;

import cz.gattserver.grass3.hw.interfaces.HWFilterTO;
import cz.gattserver.grass3.hw.interfaces.HWItemTO;
import cz.gattserver.grass3.hw.interfaces.HWItemFileTO;
import cz.gattserver.grass3.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass3.hw.interfaces.HWItemTypeTO;
import cz.gattserver.grass3.hw.interfaces.ServiceNoteTO;

public interface HWService {

	/*
	 * Images
	 */

	void saveImagesFile(InputStream in, String fileName, HWItemTO item) throws IOException;

	List<HWItemFileTO> getHWItemImagesFiles(Long id);

	long getHWItemImagesFilesCount(Long id);

	Path getHWItemImagesFilePath(Long id, String name);

	InputStream getHWItemImagesFileInputStream(Long id, String name);

	boolean deleteHWItemImagesFile(Long id, String name);

	/*
	 * Documents
	 */

	void saveDocumentsFile(InputStream in, String fileName, Long id) throws IOException;

	List<HWItemFileTO> getHWItemDocumentsFiles(Long id);

	long getHWItemDocumentsFilesCount(Long id);

	Path getHWItemDocumentsFilePath(Long id, String name);

	InputStream getHWItemDocumentsFileInputStream(Long id, String name);

	boolean deleteHWItemDocumentsFile(Long id, String name);

	/*
	 * Icons
	 */

	OutputStream createHWItemIconOutputStream(String filename, Long id);

	Path getHWItemIconFile(Long id) throws IOException;

	InputStream getHWItemIconFileInputStream(Long id);

	boolean deleteHWItemIconFile(Long id);

	/*
	 * Item types
	 */

	/**
	 * Uloží nebo aktualizuje typ hw položky
	 * 
	 * @param hwItemTypeTO
	 *            to položky
	 * @return id uložené položky
	 */
	Long saveHWType(HWItemTypeTO hwItemTypeTO);

	Set<HWItemTypeTO> getAllHWTypes();

	HWItemTypeTO getHWItemType(Long fixTypeId);

	void deleteHWItemType(Long id);

	/*
	 * Items
	 */

	Long saveHWItem(HWItemTO hwItemDTO);

	int countHWItems(HWFilterTO filter);

	List<HWItemOverviewTO> getAllHWItems();

	List<HWItemOverviewTO> getHWItems(HWFilterTO filter, int offset, int limit, OrderSpecifier<?>[] order);

	List<HWItemOverviewTO> getHWItemsByTypes(Collection<String> types);

	HWItemTO getHWItem(Long itemId);

	List<HWItemOverviewTO> getAllParts(Long usedInItemId);

	/**
	 * Získá všechny předměty, kromě předmětu jehož id je předáno jako parametr
	 * 
	 * @param itemId
	 *            id předmětu, který má být vyloučen z přehledu
	 * @return HW předměty
	 */
	List<HWItemOverviewTO> getHWItemsAvailableForPart(Long itemId);

	void deleteHWItem(Long id);

	/*
	 * Service notes
	 */

	void addServiceNote(ServiceNoteTO serviceNoteDTO, Long id);

	void modifyServiceNote(ServiceNoteTO serviceNoteDTO);

	void deleteServiceNote(ServiceNoteTO serviceNoteDTO, Long id);
}
