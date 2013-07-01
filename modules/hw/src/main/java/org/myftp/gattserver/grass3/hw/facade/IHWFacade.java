package org.myftp.gattserver.grass3.hw.facade;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;

import org.myftp.gattserver.grass3.hw.dto.HWItemDTO;
import org.myftp.gattserver.grass3.hw.dto.HWItemTypeDTO;
import org.myftp.gattserver.grass3.hw.dto.ServiceNoteDTO;

public interface IHWFacade {

	public Set<HWItemTypeDTO> getAllHWTypes();

	public List<HWItemDTO> getAllHWItems();

	public List<HWItemDTO> getHWItemsAvailableForPart(HWItemDTO item);

	public List<ServiceNoteDTO> getAllServiceNotes();

	public boolean saveHWType(String name);

	public boolean saveHWItem(HWItemDTO hwItemDTO);

	public boolean deleteHWItem(HWItemDTO hwItem);

	public boolean addServiceNote(ServiceNoteDTO serviceNoteDTO,
			HWItemDTO hwItem);

	public boolean deleteHWItemType(HWItemTypeDTO hwItemType);

	public HWItemDTO getHWItem(Long itemId);

	public List<HWItemDTO> getAllParts(Long usedInItemId);

	public String getHWItemUploadDir(HWItemDTO item);

	public boolean saveImagesFile(File file, String fileName, HWItemDTO item);

	public String getTmpDir();

	public File getHWItemIconFile(HWItemDTO itemDTO);

	public boolean deleteHWItemIconFile(HWItemDTO hwItem);

	public OutputStream createHWItemIconOutputStream(String filename,
			HWItemDTO hwItem) throws FileNotFoundException;

	public String getHWItemImagesUploadDir(HWItemDTO item);

	public String getHWItemDocumentsUploadDir(HWItemDTO item);

	boolean saveDocumentsFile(File file, String fileName, HWItemDTO item);

	public File[] getHWItemImagesFiles(HWItemDTO itemDTO);

	public boolean deleteHWItemFile(HWItemDTO hwItem, File file);

	public File[] getHWItemDocumentsFiles(HWItemDTO itemDTO);

}
