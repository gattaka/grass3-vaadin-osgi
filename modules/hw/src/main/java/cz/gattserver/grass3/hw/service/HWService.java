package cz.gattserver.grass3.hw.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;

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

	public boolean saveImagesFile(InputStream in, String fileName, HWItemTO item);

	public List<HWItemFileTO> getHWItemImagesFiles(HWItemTO itemDTO);

	public InputStream getHWItemImagesFileInputStream(HWItemTO hwItem, String name);

	public boolean deleteHWItemImagesFile(HWItemTO hwItem, String name);

	/*
	 * Documents
	 */

	public boolean saveDocumentsFile(InputStream in, String fileName, HWItemTO item);

	public List<HWItemFileTO> getHWItemDocumentsFiles(HWItemTO itemDTO);

	public InputStream getHWItemDocumentsFileInputStream(HWItemTO hwItem, String name);

	public boolean deleteHWItemDocumentsFile(HWItemTO hwItem, String name);

	/*
	 * Icons
	 */

	public OutputStream createHWItemIconOutputStream(String filename, HWItemTO hwItem);

	public InputStream getHWItemIconFileInputStream(HWItemTO hwItem);

	public boolean deleteHWItemIconFile(HWItemTO hwItem);

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
	public Long saveHWType(HWItemTypeTO hwItemTypeTO);

	public Set<HWItemTypeTO> getAllHWTypes();

	public HWItemTypeTO getHWItemType(Long fixTypeId);

	public void deleteHWItemType(Long id);

	/*
	 * Items
	 */

	public Long saveHWItem(HWItemTO hwItemDTO);

	public int countHWItems(HWFilterTO filter);

	public List<HWItemOverviewTO> getAllHWItems();

	public List<HWItemOverviewTO> getHWItems(HWFilterTO filter, Pageable pageable, OrderSpecifier<?>[] order);

	public List<HWItemOverviewTO> getHWItemsByTypes(Collection<String> types);

	public HWItemTO getHWItem(Long itemId);

	public List<HWItemOverviewTO> getAllParts(Long usedInItemId);

	/**
	 * Získá všechny předměty, kromě předmětu jehož id je předáno jako parametr
	 * 
	 * @param itemId
	 *            id předmětu, který má být vyloučen z přehledu
	 * @return HW předměty
	 */
	public List<HWItemOverviewTO> getHWItemsAvailableForPart(Long itemId);

	public void deleteHWItem(Long id);

	/*
	 * Service notes
	 */

	public void addServiceNote(ServiceNoteTO serviceNoteDTO, HWItemTO hwItemDTO);

	public void modifyServiceNote(ServiceNoteTO serviceNoteDTO);

	public void deleteServiceNote(ServiceNoteTO serviceNoteDTO, HWItemTO hwItemDTO);
}
