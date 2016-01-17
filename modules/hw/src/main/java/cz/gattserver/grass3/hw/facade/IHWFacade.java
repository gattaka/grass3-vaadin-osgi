package cz.gattserver.grass3.hw.facade;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import cz.gattserver.grass3.hw.dto.HWItemDTO;
import cz.gattserver.grass3.hw.dto.HWItemTypeDTO;
import cz.gattserver.grass3.hw.dto.ServiceNoteDTO;

public interface IHWFacade {

	public Set<HWItemTypeDTO> getAllHWTypes();
	
	public HWItemTypeDTO getHWItemType(Long fixTypeId);

	public List<HWItemDTO> getAllHWItems();

	public List<HWItemDTO> getHWItemsByTypes(Collection<String> types);

	public List<HWItemDTO> getHWItemsAvailableForPart(HWItemDTO item);

	public List<ServiceNoteDTO> getAllServiceNotes();

	public boolean saveHWType(HWItemTypeDTO hwItemTypeDTO);

	public boolean saveHWItem(HWItemDTO hwItemDTO);

	public boolean deleteHWItem(HWItemDTO hwItem);

	public boolean addServiceNote(ServiceNoteDTO serviceNoteDTO, HWItemDTO hwItem);

	public boolean deleteHWItemType(HWItemTypeDTO hwItemType);

	public HWItemDTO getHWItem(Long itemId);

	public List<HWItemDTO> getAllParts(Long usedInItemId);

	public String getHWItemUploadDir(HWItemDTO item);

	public boolean saveImagesFile(InputStream in, String fileName, HWItemDTO item);

	public String getTmpDir();

	public File getHWItemIconFile(HWItemDTO itemDTO);

	public boolean deleteHWItemIconFile(HWItemDTO hwItem);

	public OutputStream createHWItemIconOutputStream(String filename, HWItemDTO hwItem) throws FileNotFoundException;

	public String getHWItemImagesUploadDir(HWItemDTO item);

	public String getHWItemDocumentsUploadDir(HWItemDTO item);

	boolean saveDocumentsFile(InputStream in, String fileName, HWItemDTO item);

	public File[] getHWItemImagesFiles(HWItemDTO itemDTO);

	public boolean deleteHWItemFile(HWItemDTO hwItem, File file);

	public File[] getHWItemDocumentsFiles(HWItemDTO itemDTO);

	void modifyServiceNote(ServiceNoteDTO serviceNoteDTO);

	public void deleteServiceNote(ServiceNoteDTO bean, HWItemDTO hwItem);

	

}
