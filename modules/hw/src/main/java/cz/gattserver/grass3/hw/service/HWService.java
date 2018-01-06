package cz.gattserver.grass3.hw.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.OrderSpecifier;

import cz.gattserver.grass3.hw.interfaces.HWFilterDTO;
import cz.gattserver.grass3.hw.interfaces.HWItemDTO;
import cz.gattserver.grass3.hw.interfaces.HWItemOverviewDTO;
import cz.gattserver.grass3.hw.interfaces.HWItemTypeDTO;
import cz.gattserver.grass3.hw.interfaces.ServiceNoteDTO;

public interface HWService {

	Set<HWItemTypeDTO> getAllHWTypes();

	HWItemTypeDTO getHWItemType(Long fixTypeId);

	List<HWItemOverviewDTO> getAllHWItems();

	List<HWItemOverviewDTO> getHWItemsByTypes(Collection<String> types);

	List<HWItemOverviewDTO> getHWItemsAvailableForPart(HWItemDTO item);

	void saveHWType(HWItemTypeDTO hwItemTypeDTO);

	void saveHWItem(HWItemDTO hwItemDTO);

	void deleteHWItem(Long id);

	void addServiceNote(ServiceNoteDTO serviceNoteDTO, HWItemDTO hwItem);

	void deleteHWItemType(Long id);

	HWItemDTO getHWItem(Long itemId);

	List<HWItemOverviewDTO> getAllParts(Long usedInItemId);

	String getHWItemUploadDir(HWItemDTO item);

	boolean saveImagesFile(InputStream in, String fileName, HWItemDTO item);

	String getTmpDir();

	File getHWItemIconFile(HWItemDTO itemDTO);

	boolean deleteHWItemIconFile(HWItemDTO hwItem);

	OutputStream createHWItemIconOutputStream(String filename, HWItemDTO hwItem) throws FileNotFoundException;

	String getHWItemImagesUploadDir(HWItemDTO item);

	String getHWItemDocumentsUploadDir(HWItemDTO item);

	boolean saveDocumentsFile(InputStream in, String fileName, HWItemDTO item);

	File[] getHWItemImagesFiles(HWItemDTO itemDTO);

	boolean deleteHWItemFile(HWItemDTO hwItem, File file);

	File[] getHWItemDocumentsFiles(HWItemDTO itemDTO);

	void modifyServiceNote(ServiceNoteDTO serviceNoteDTO);

	void deleteServiceNote(ServiceNoteDTO bean, HWItemDTO hwItem);

	/**
	 * Zjistí počet HW položek dle filtru
	 */
	int countHWItems(HWFilterDTO filter);

	List<HWItemOverviewDTO> getHWItems(HWFilterDTO filter, Pageable pageable, OrderSpecifier<?>[] order);

}
