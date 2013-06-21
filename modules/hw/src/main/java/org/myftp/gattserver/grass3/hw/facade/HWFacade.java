package org.myftp.gattserver.grass3.hw.facade;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.SpringContextHelper;
import org.myftp.gattserver.grass3.config.IConfigurationService;
import org.myftp.gattserver.grass3.hw.config.HWConfiguration;
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
		if (hwItemDTO.getUsedIn() != null) {
			HWItem usedIn = hwItemRepository.findOne(hwItemDTO.getUsedIn()
					.getId());
			item.setUsedIn(usedIn);
		}
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

		item.setServiceNotes(null);
		hwItemRepository.save(item);

		for (HWItem targetItem : hwItemRepository.findByUsedInId(item.getId())) {
			targetItem.setUsedIn(null);
			hwItemRepository.save(targetItem);
		}

		hwItemRepository.delete(item.getId());

		return true;
	}

	/**
	 * Vygeneruje {@link ServiceNote} o přidání/odebrání HW, uloží a přidá k
	 * cílovému HW
	 * 
	 * @param triggerItem
	 *            HW který je přidán/odebrán
	 * @param triggerNote
	 *            {@link ServiceNote}, který událost spustil
	 * @param added
	 *            {@code true} pokud byl HW přidán
	 */
	private void saveHWPartMoveServiceNote(HWItem triggerItem,
			ServiceNote triggerNote, boolean added) {
		HWItem targetItem = hwItemRepository.findOne(triggerItem.getUsedIn()
				.getId());
		ServiceNote removeNote = new ServiceNote();
		removeNote.setDate(triggerNote.getDate());

		StringBuilder builder = new StringBuilder();
		builder.append(added ? "Byl přidán:" : "Byl odebrán:").append("\n")
				.append(triggerItem.getName()).append("\n\n").append("Důvod:")
				.append("\n").append(triggerNote.getDescription());
		removeNote.setDescription(builder.toString());
		removeNote.setState(targetItem.getState());
		removeNote.setUsage(targetItem.getUsedIn() == null ? "" : targetItem
				.getUsedIn().getName());
		ServiceNote note = serviceNoteRepository.save(removeNote);
		targetItem.getServiceNotes().add(note);
		hwItemRepository.save(targetItem);
	}

	@Override
	public boolean addServiceNote(ServiceNoteDTO serviceNoteDTO,
			HWItemDTO hwItemDTO) {

		HWItem item = hwItemRepository.findOne(hwItemDTO.getId());
		ServiceNote serviceNote = new ServiceNote();
		serviceNote.setDate(serviceNoteDTO.getDate());
		serviceNote.setDescription(serviceNoteDTO.getDescription());
		serviceNote.setState(serviceNoteDTO.getState());
		serviceNote.setUsage(serviceNoteDTO.getUsedIn() == null ? ""
				: serviceNoteDTO.getUsedIn().getName());
		serviceNoteRepository.save(serviceNote);

		if (item.getServiceNotes() == null)
			item.setServiceNotes(new ArrayList<ServiceNote>());
		item.getServiceNotes().add(serviceNote);
		item.setState(serviceNote.getState());

		HWItem oldTarget = item.getUsedIn();

		// HW je někde součástí
		if (serviceNoteDTO.getUsedIn() != null) {

			// cílový HW, kde je nyní HW součástí
			HWItem targetItem = hwItemRepository.findOne(serviceNoteDTO
					.getUsedIn().getId());

			// předtím nebyl nikde součástí
			if (oldTarget == null) {
				item.setUsedIn(targetItem);
				saveHWPartMoveServiceNote(item, serviceNote, true);
			} else if (oldTarget.getId() != serviceNoteDTO.getUsedIn().getId()) {
				// již předtím byl součástí, ale nyní je jinde
				saveHWPartMoveServiceNote(item, serviceNote, false);
				item.setUsedIn(targetItem);
				saveHWPartMoveServiceNote(item, serviceNote, true);
			} else {
				// nic se nezměnilo - HW je stále součástí stejného HW
			}

		} else { // HW není nikde součástí

			// už předtím nebyl nikde součástí
			if (oldTarget == null) {
				// nic se nezměnilo - HW stále není nikde evidován jako součást
			} else {
				// předtím někde byl
				saveHWPartMoveServiceNote(item, serviceNote, false);
				item.setUsedIn(null);
			}
		}

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
	public HWItemDTO getHWItem(Long itemId) {
		return hwMapper.mapHWItem(hwItemRepository.findOne(itemId));
	}

	@Override
	public List<HWItemDTO> getAllParts(Long usedInItemId) {
		return hwMapper.mapHWItems(hwItemRepository
				.findByUsedInId(usedInItemId));
	}

	@Override
	public List<HWItemDTO> getHWItemsAvailableForPart(HWItemDTO item) {
		List<HWItemDTO> items = getAllHWItems();
		items.remove(item);
		return items;
	}

	@Override
	public boolean saveFile(File file, String fileName, HWItemDTO hwItem) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<File> getFilesFromHW(HWItemDTO hwItem) {
		IConfigurationService configurationService = (IConfigurationService) SpringContextHelper
				.getBean("configurationService");
		HWConfiguration configuration = new HWConfiguration();
		configurationService.loadConfiguration(configuration);

		File hwDir = new File(configuration.getRootDir(), hwItem.getId()
				.toString());

		if (hwDir.exists() == false)
			return new ArrayList<File>();

		return Arrays.asList(hwDir.listFiles());
	}
}
