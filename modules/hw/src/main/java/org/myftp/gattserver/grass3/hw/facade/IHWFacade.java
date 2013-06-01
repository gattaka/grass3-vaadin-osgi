package org.myftp.gattserver.grass3.hw.facade;

import java.util.List;
import java.util.Set;

import org.myftp.gattserver.grass3.hw.dto.HWItemDTO;
import org.myftp.gattserver.grass3.hw.dto.HWItemTypeDTO;
import org.myftp.gattserver.grass3.hw.dto.ServiceNoteDTO;

public interface IHWFacade {

	public Set<HWItemTypeDTO> getAllHWTypes();

	public List<HWItemDTO> getAllHWItems();

	public List<ServiceNoteDTO> getAllServiceNotes();

	public boolean saveHWType(String name);

	public boolean saveHWItem(HWItemDTO hwItemDTO);

	public boolean deleteHWItem(HWItemDTO hwItem);

	public boolean addServiceNote(ServiceNoteDTO serviceNoteDTO,
			HWItemDTO hwItem);

	public boolean deleteHWItemType(HWItemTypeDTO hwItemType);

	public HWItemDTO getHWItem(Long fixItemId);

}
