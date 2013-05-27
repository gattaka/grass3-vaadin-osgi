package org.myftp.gattserver.grass3.hw.facade;

import java.util.List;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.hw.dao.HWItemRepository;
import org.myftp.gattserver.grass3.hw.dao.HWItemTypeRepository;
import org.myftp.gattserver.grass3.hw.dao.ServiceNoteRepository;
import org.myftp.gattserver.grass3.hw.domain.HWItem;
import org.myftp.gattserver.grass3.hw.domain.HWItemType;
import org.myftp.gattserver.grass3.hw.domain.ServiceNote;
import org.myftp.gattserver.grass3.hw.dto.HWItemDTO;
import org.myftp.gattserver.grass3.hw.dto.HWItemTypeDTO;
import org.myftp.gattserver.grass3.hw.dto.ServiceNoteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component("hwFacade")
public class HWFacade implements IHWFacade {

	@Autowired
	private HWItemRepository hwItemRepository;

	@Autowired
	private HWItemTypeRepository hwItemTypeRepository;

	@Autowired
	private ServiceNoteRepository serviceNoteRepository;

	@Resource(name = "hwMapper")
	private HWMapper hwMapper;

	@Override
	public List<HWItemTypeDTO> getAllHWTypes() {
		List<HWItemType> hwItemTypes = hwItemTypeRepository.findAll();
		return hwMapper.mapHWItemTypes(hwItemTypes);
	}

	@Override
	public List<HWItemDTO> getAllHWItems() {
		List<HWItem> hwItemTypes = hwItemRepository.findAll();
		return hwMapper.mapHWItems(hwItemTypes);
	}

	@Override
	public List<ServiceNoteDTO> getAllServiceNotes() {
		List<ServiceNote> hwItemTypes = serviceNoteRepository.findAll();
		return hwMapper.mapServiceNotes(hwItemTypes);
	}

	@Override
	public boolean saveHWType(String value) {
		HWItemType type = new HWItemType();
		type.setName(value);
		return hwItemTypeRepository.save(type) != null;
	}

	@Override
	public boolean saveHWItem(HWItemDTO hwItemDTO) {
		HWItem item = new HWItem();
		item.setName(hwItemDTO.getName());
		item.setPurchaseDate(hwItemDTO.getPurchaseDate());
		item.setDestructionDate(hwItemDTO.getDestructionDate());
		item.setPrice(hwItemDTO.getPrice());
		item.setState(hwItemDTO.getState());
		return hwItemRepository.save(item) != null;
	}

}
