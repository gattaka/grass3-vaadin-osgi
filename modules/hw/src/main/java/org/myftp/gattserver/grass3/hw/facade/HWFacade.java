package org.myftp.gattserver.grass3.hw.facade;

import java.util.List;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.hw.dao.HWItemDAO;
import org.myftp.gattserver.grass3.hw.dao.HWItemTypeDAO;
import org.myftp.gattserver.grass3.hw.dao.ServiceNoteDAO;
import org.myftp.gattserver.grass3.hw.domain.HWItem;
import org.myftp.gattserver.grass3.hw.domain.HWItemType;
import org.myftp.gattserver.grass3.hw.domain.ServiceNote;
import org.myftp.gattserver.grass3.hw.dto.HWItemDTO;
import org.myftp.gattserver.grass3.hw.dto.HWItemTypeDTO;
import org.myftp.gattserver.grass3.hw.dto.ServiceNoteDTO;
import org.springframework.stereotype.Component;

@Component("hwFacade")
public class HWFacade implements IHWFacade {

	@Resource(name = "hwItemTypeDAO")
	private HWItemTypeDAO hwItemTypeDAO;

	@Resource(name = "hwItemDAO")
	private HWItemDAO hwItemDAO;

	@Resource(name = "serviceNoteDAO")
	private ServiceNoteDAO serviceNoteDAO;

	@Resource(name = "hwMapper")
	private HWMapper hwMapper;

	public List<HWItemTypeDTO> getAllHWTypes() {
		List<HWItemType> hwItemTypes = hwItemTypeDAO.findAll();
		return hwMapper.mapHWItemTypes(hwItemTypes);
	}

	public List<HWItemDTO> getAllHWItems() {
		List<HWItem> hwItemTypes = hwItemDAO.findAll();
		return hwMapper.mapHWItems(hwItemTypes);
	}

	public List<ServiceNoteDTO> getAllServiceNotes() {
		List<ServiceNote> hwItemTypes = serviceNoteDAO.findAll();
		return hwMapper.mapServiceNotes(hwItemTypes);
	}

}
