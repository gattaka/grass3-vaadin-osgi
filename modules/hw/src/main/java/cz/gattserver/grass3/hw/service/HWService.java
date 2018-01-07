package cz.gattserver.grass3.hw.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.OrderSpecifier;

import cz.gattserver.grass3.hw.interfaces.HWFilterDTO;
import cz.gattserver.grass3.hw.interfaces.HWItemDTO;
import cz.gattserver.grass3.hw.interfaces.HWItemFileTO;
import cz.gattserver.grass3.hw.interfaces.HWItemOverviewDTO;
import cz.gattserver.grass3.hw.interfaces.HWItemTypeDTO;
import cz.gattserver.grass3.hw.interfaces.ServiceNoteDTO;

public interface HWService {

	/*
	 * Images
	 */

	public boolean saveImagesFile(InputStream in, String fileName, HWItemDTO item);

	public List<HWItemFileTO> getHWItemImagesFiles(HWItemDTO itemDTO);

	public InputStream getHWItemImagesFileInputStream(HWItemDTO hwItem, String name);

	public boolean deleteHWItemImagesFile(HWItemDTO hwItem, String name);

	/*
	 * Documents
	 */

	public boolean saveDocumentsFile(InputStream in, String fileName, HWItemDTO item);

	public List<HWItemFileTO> getHWItemDocumentsFiles(HWItemDTO itemDTO);

	public InputStream getHWItemDocumentsFileInputStream(HWItemDTO hwItem, String name);

	public boolean deleteHWItemDocumentsFile(HWItemDTO hwItem, String name);

	/*
	 * Icons
	 */

	public OutputStream createHWItemIconOutputStream(String filename, HWItemDTO hwItem);

	public InputStream getHWItemIconFileInputStream(HWItemDTO hwItem);

	public boolean deleteHWItemIconFile(HWItemDTO hwItem);

	/*
	 * Item types
	 */

	public void saveHWType(HWItemTypeDTO hwItemTypeDTO);

	public Set<HWItemTypeDTO> getAllHWTypes();

	public HWItemTypeDTO getHWItemType(Long fixTypeId);

	public void deleteHWItemType(Long id);

	/*
	 * Items
	 */

	public void saveHWItem(HWItemDTO hwItemDTO);

	public int countHWItems(HWFilterDTO filter);

	public List<HWItemOverviewDTO> getAllHWItems();

	public List<HWItemOverviewDTO> getHWItems(HWFilterDTO filter, Pageable pageable, OrderSpecifier<?>[] order);

	public List<HWItemOverviewDTO> getHWItemsByTypes(Collection<String> types);

	public HWItemDTO getHWItem(Long itemId);

	public List<HWItemOverviewDTO> getAllParts(Long usedInItemId);

	public List<HWItemOverviewDTO> getHWItemsAvailableForPart(HWItemDTO item);

	public void deleteHWItem(Long id);

	/*
	 * Service notes
	 */

	public void addServiceNote(ServiceNoteDTO serviceNoteDTO, HWItemDTO hwItemDTO);

	public void modifyServiceNote(ServiceNoteDTO serviceNoteDTO);

	public void deleteServiceNote(ServiceNoteDTO serviceNoteDTO, HWItemDTO hwItemDTO);
}
