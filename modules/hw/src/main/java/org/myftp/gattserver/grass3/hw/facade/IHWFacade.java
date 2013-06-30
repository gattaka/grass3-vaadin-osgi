package org.myftp.gattserver.grass3.hw.facade;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;

import org.myftp.gattserver.grass3.hw.dto.HWItemDTO;
import org.myftp.gattserver.grass3.hw.dto.HWItemFileDTO;
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

	public boolean addHWItemFile(HWItemFileDTO hwItemFileDTO,
			HWItemDTO hwItemDTO, boolean document);

	public boolean deleteHWItemType(HWItemTypeDTO hwItemType);

	public HWItemDTO getHWItem(Long itemId);

	public List<HWItemDTO> getAllParts(Long usedInItemId);

	public String getUploadDir(HWItemDTO item);

	public boolean saveFile(File file, String fileName, HWItemDTO item);

	public String getTmpDir();

	public File getHWItemImageFile(HWItemDTO itemDTO);

	public boolean deleteHWItemImageFile(HWItemDTO hwItem);

	public OutputStream createHWItemImageOutputStream(String filename,
			HWItemDTO hwItem) throws FileNotFoundException;

}
