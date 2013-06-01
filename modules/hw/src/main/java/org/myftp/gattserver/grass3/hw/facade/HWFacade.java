package org.myftp.gattserver.grass3.hw.facade;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	public Set<HWItemTypeDTO> getAllHWTypes() {
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
		HWItem item;
		if (hwItemDTO.getId() == null)
			item = new HWItem();
		else
			item = hwItemRepository.findOne(hwItemDTO.getId());
		item.setName(hwItemDTO.getName());
		item.setPurchaseDate(hwItemDTO.getPurchaseDate());
		item.setDestructionDate(hwItemDTO.getDestructionDate());
		item.setPrice(hwItemDTO.getPrice());
		item.setState(hwItemDTO.getState());
		item.setUsage(hwItemDTO.getUsage());
		item.setWarrantyYears(hwItemDTO.getWarrantyYears());
		if (hwItemDTO.getTypes() != null) {
			item.setTypes(new HashSet<HWItemType>());
			for (HWItemTypeDTO typeDTO : hwItemDTO.getTypes()) {
				HWItemType type = hwItemTypeRepository.findOne(typeDTO.getId());
				item.getTypes().add(type);
			}
		}
		return hwItemRepository.save(item) != null;
	}

	@Override
	public boolean deleteHWItem(HWItemDTO hwItem) {

		HWItem item = hwItemRepository.findOne(hwItem.getId());
		for (ServiceNote note : item.getServiceNotes()) {
			serviceNoteRepository.delete(note);
		}
		hwItemRepository.delete(item.getId());

		return true;
	}

	@Override
	public boolean addServiceNote(ServiceNoteDTO serviceNoteDTO,
			HWItemDTO hwItem) {

		HWItem item = hwItemRepository.findOne(hwItem.getId());
		ServiceNote serviceNote = new ServiceNote();
		serviceNote.setDate(serviceNoteDTO.getDate());
		serviceNote.setDescription(serviceNoteDTO.getDescription());
		serviceNote.setState(serviceNoteDTO.getState());
		serviceNote.setUsage(serviceNoteDTO.getUsage());
		serviceNoteRepository.save(serviceNote);

		if (item.getServiceNotes() == null)
			item.setServiceNotes(new ArrayList<ServiceNote>());
		item.getServiceNotes().add(serviceNote);
		item.setState(serviceNote.getState());
		item.setUsage(serviceNote.getUsage());

		hwItemRepository.save(item);

		return true;
	}

	@Override
	public boolean deleteHWItemType(HWItemTypeDTO hwItemType) {

		HWItemType itemType = hwItemTypeRepository.findOne(hwItemType.getId());

		List<HWItem> items = hwItemRepository.findByTypesId(itemType.getId());
		for (HWItem item : items) {
			item.getTypes().remove(itemType);
			hwItemRepository.save(item);
		}

		hwItemTypeRepository.delete(itemType);

		return true;
	}

	@Override
	public HWItemDTO getHWItem(Long fixItemId) {
		return hwMapper.mapHWItem(hwItemRepository.findOne(fixItemId));
	}
}
