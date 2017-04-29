package cz.gattserver.grass3.hw.facade;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.OrderSpecifier;

import cz.gattserver.grass3.hw.dto.HWFilterDTO;
import cz.gattserver.grass3.hw.dto.HWItemDTO;
import cz.gattserver.grass3.hw.dto.HWItemOverviewDTO;
import cz.gattserver.grass3.hw.dto.HWItemTypeDTO;
import cz.gattserver.grass3.hw.dto.ServiceNoteDTO;

public interface IHWFacade {

	Set<HWItemTypeDTO> getAllHWTypes();

	HWItemTypeDTO getHWItemType(Long fixTypeId);

	List<HWItemOverviewDTO> getAllHWItems();

	List<HWItemOverviewDTO> getHWItemsByTypes(Collection<String> types);

	List<HWItemOverviewDTO> getHWItemsAvailableForPart(HWItemDTO item);

	boolean saveHWType(HWItemTypeDTO hwItemTypeDTO);

	boolean saveHWItem(HWItemDTO hwItemDTO);

	boolean deleteHWItem(HWItemDTO hwItem);

	boolean addServiceNote(ServiceNoteDTO serviceNoteDTO, HWItemDTO hwItem);

	boolean deleteHWItemType(HWItemTypeDTO hwItemType);

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
	long countHWItems(HWFilterDTO filter);

	List<HWItemOverviewDTO> getHWItems(HWFilterDTO filter, Pageable pageable, OrderSpecifier<?>[] order);

}
