package cz.gattserver.grass3.hw.facade;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.OrderSpecifier;

import cz.gattserver.grass3.config.ConfigurationService;
import cz.gattserver.grass3.hw.HWConfiguration;
import cz.gattserver.grass3.hw.dao.HWItemRepository;
import cz.gattserver.grass3.hw.dao.HWItemTypeRepository;
import cz.gattserver.grass3.hw.dao.ServiceNoteRepository;
import cz.gattserver.grass3.hw.domain.HWItem;
import cz.gattserver.grass3.hw.domain.HWItemType;
import cz.gattserver.grass3.hw.domain.ServiceNote;
import cz.gattserver.grass3.hw.dto.HWFilterDTO;
import cz.gattserver.grass3.hw.dto.HWItemDTO;
import cz.gattserver.grass3.hw.dto.HWItemOverviewDTO;
import cz.gattserver.grass3.hw.dto.HWItemTypeDTO;
import cz.gattserver.grass3.hw.dto.ServiceNoteDTO;

@Transactional
@Component
public class HWFacadeImpl implements HWFacade {

	@Autowired
	private HWItemRepository hwItemRepository;

	@Autowired
	private HWItemTypeRepository hwItemTypeRepository;

	@Autowired
	private ServiceNoteRepository serviceNoteRepository;

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private HWMapper hwMapper;

	/**
	 * Získá aktuální konfiguraci ze souboru konfigurace
	 * 
	 * @return soubor konfigurace FM
	 * @throws JAXBException
	 */
	private HWConfiguration loadConfiguration() {
		HWConfiguration configuration = new HWConfiguration();
		configurationService.loadConfiguration(configuration);
		return configuration;
	}

	@Override
	public Set<HWItemTypeDTO> getAllHWTypes() {
		List<HWItemType> hwItemTypes = hwItemTypeRepository.findListOrderByName();
		return hwMapper.mapHWItemTypes(hwItemTypes);
	}

	@Override
	public List<HWItemOverviewDTO> getAllHWItems() {
		List<HWItem> hwItemTypes = hwItemRepository.findAll();
		return hwMapper.mapHWItems(hwItemTypes);
	}

	@Override
	public List<HWItemOverviewDTO> getHWItemsByTypes(Collection<String> types) {
		List<HWItem> hwItemTypes = hwItemRepository.getHWItemsByTypes(types);
		return hwMapper.mapHWItems(hwItemTypes);
	}

	@Override
	public boolean saveHWType(HWItemTypeDTO hwItemTypeDTO) {
		HWItemType type = hwMapper.mapHWItem(hwItemTypeDTO);
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
		item.setSupervizedFor(hwItemDTO.getSupervizedFor());
		if (hwItemDTO.getUsedIn() != null) {
			HWItem usedIn = hwItemRepository.findOne(hwItemDTO.getUsedIn().getId());
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
	private void saveHWPartMoveServiceNote(HWItem triggerItem, ServiceNote triggerNote, boolean added) {
		HWItem targetItem = hwItemRepository.findOne(triggerItem.getUsedIn().getId());
		ServiceNote removeNote = new ServiceNote();
		removeNote.setDate(triggerNote.getDate());

		StringBuilder builder = new StringBuilder();
		builder.append(added ? "Byl přidán:" : "Byl odebrán:").append("\n").append(triggerItem.getName()).append("\n\n")
				.append("Důvod:").append("\n").append(triggerNote.getDescription());
		removeNote.setDescription(builder.toString());
		removeNote.setState(targetItem.getState());
		removeNote.setUsage(targetItem.getUsedIn() == null ? "" : targetItem.getUsedIn().getName());
		ServiceNote note = serviceNoteRepository.save(removeNote);
		targetItem.getServiceNotes().add(note);
		hwItemRepository.save(targetItem);
	}

	@Override
	public void modifyServiceNote(ServiceNoteDTO serviceNoteDTO) {
		ServiceNote serviceNote = serviceNoteRepository.findOne(serviceNoteDTO.getId());
		serviceNote.setDate(serviceNoteDTO.getDate());
		serviceNote.setDescription(serviceNoteDTO.getDescription());
		serviceNote.setState(serviceNoteDTO.getState());
		serviceNote.setUsage(serviceNoteDTO.getUsedIn() == null ? "" : serviceNoteDTO.getUsedIn().getName());
		serviceNoteRepository.save(serviceNote);
	}

	@Override
	public void deleteServiceNote(ServiceNoteDTO serviceNoteDTO, HWItemDTO hwItemDTO) {
		HWItem item = hwItemRepository.findOne(hwItemDTO.getId());
		ServiceNote serviceNote = serviceNoteRepository.findOne(serviceNoteDTO.getId());
		item.getServiceNotes().remove(serviceNote);
		hwItemRepository.save(item);
		serviceNoteRepository.delete(serviceNote);
	}

	@Override
	public boolean addServiceNote(ServiceNoteDTO serviceNoteDTO, HWItemDTO hwItemDTO) {

		HWItem item = hwItemRepository.findOne(hwItemDTO.getId());
		ServiceNote serviceNote = new ServiceNote();
		serviceNote.setDate(serviceNoteDTO.getDate());
		serviceNote.setDescription(serviceNoteDTO.getDescription());
		serviceNote.setState(serviceNoteDTO.getState());
		serviceNote.setUsage(serviceNoteDTO.getUsedIn() == null ? "" : serviceNoteDTO.getUsedIn().getName());
		serviceNote = serviceNoteRepository.save(serviceNote);
		serviceNoteDTO.setId(serviceNote.getId());

		if (item.getServiceNotes() == null)
			item.setServiceNotes(new ArrayList<ServiceNote>());
		item.getServiceNotes().add(serviceNote);
		item.setState(serviceNote.getState());

		HWItem oldTarget = item.getUsedIn();

		// HW je někde součástí
		if (serviceNoteDTO.getUsedIn() != null) {

			// cílový HW, kde je nyní HW součástí
			HWItem targetItem = hwItemRepository.findOne(serviceNoteDTO.getUsedIn().getId());

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
	public List<HWItemOverviewDTO> getAllParts(Long usedInItemId) {
		return hwMapper.mapHWItems(hwItemRepository.findByUsedInId(usedInItemId));
	}

	@Override
	public List<HWItemOverviewDTO> getHWItemsAvailableForPart(HWItemDTO item) {
		List<HWItemOverviewDTO> items = getAllHWItems();
		items.remove(item);
		return items;
	}

	@Override
	public String getHWItemUploadDir(HWItemDTO item) {
		HWConfiguration configuration;
		configuration = loadConfiguration();
		File file = new File(configuration.getRootDir(), item.getId().toString());
		if (file.exists() == false)
			if (file.mkdirs() == false)
				return null;
		return file.getAbsolutePath();
	}

	private String getHWItemSubUploadDir(HWItemDTO item, String dirname) {
		String dir = getHWItemUploadDir(item);
		if (dir == null)
			return null;

		File file = new File(dir, dirname);
		if (file.exists() == false)
			if (file.mkdirs() == false)
				return null;
		return file.getAbsolutePath();
	}

	@Override
	public String getHWItemImagesUploadDir(HWItemDTO item) {
		return getHWItemSubUploadDir(item, "images");
	}

	@Override
	public String getHWItemDocumentsUploadDir(HWItemDTO item) {
		return getHWItemSubUploadDir(item, "documents");
	}

	@Override
	public boolean saveImagesFile(InputStream in, String fileName, HWItemDTO item) {
		Path path = Paths.get(getHWItemImagesUploadDir(item), fileName);
		try {
			Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean saveDocumentsFile(InputStream in, String fileName, HWItemDTO item) {
		Path path = Paths.get(getHWItemDocumentsUploadDir(item), fileName);
		try {
			Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public File[] getHWItemImagesFiles(HWItemDTO itemDTO) {
		File hwItemImagesDir = new File(getHWItemImagesUploadDir(itemDTO));

		if (hwItemImagesDir.exists() == false || hwItemImagesDir.isDirectory() == false)
			return null;

		return hwItemImagesDir.listFiles();
	}

	@Override
	public File[] getHWItemDocumentsFiles(HWItemDTO itemDTO) {
		File hwItemDocumentsDir = new File(getHWItemDocumentsUploadDir(itemDTO));

		if (hwItemDocumentsDir.exists() == false || hwItemDocumentsDir.isDirectory() == false)
			return null;

		return hwItemDocumentsDir.listFiles();
	}

	@Override
	public File getHWItemIconFile(HWItemDTO itemDTO) {
		HWConfiguration configuration;
		configuration = loadConfiguration();
		File hwItemDir = new File(configuration.getRootDir(), itemDTO.getId().toString());

		if (hwItemDir.exists() == false || hwItemDir.isDirectory() == false)
			return null;

		// ikona HW je nějaký obrázek s názvem "icon-###.@@@",
		// kde ### je hash a @@@ je přípona
		for (File hwItemFile : hwItemDir.listFiles()) {
			if (hwItemFile.getName().matches("icon-[^\\.]*\\.[^\\.]*"))
				return hwItemFile;
		}

		return null;
	}

	@Override
	public String getTmpDir() {
		HWConfiguration configuration;
		configuration = loadConfiguration();
		File file = new File(configuration.getTmpDir());
		if (file.exists() == false)
			if (file.mkdirs() == false)
				return null;
		return file.getAbsolutePath();
	}

	@Override
	public boolean deleteHWItemIconFile(HWItemDTO hwItem) {
		File image = getHWItemIconFile(hwItem);
		if (image == null)
			return true;

		return image.delete();
	}

	@Override
	public OutputStream createHWItemIconOutputStream(String filename, HWItemDTO hwItem) throws FileNotFoundException {
		String[] parts = filename.split("\\.");
		String extension = parts.length >= 1 ? parts[parts.length - 1] : "";

		long time = System.currentTimeMillis();
		int uniqueHash = (String.valueOf(time) + hwItem.getName()).hashCode();

		File file = new File(getHWItemUploadDir(hwItem), "icon-" + uniqueHash + "." + extension);
		return new FileOutputStream(file);
	}

	@Override
	public boolean deleteHWItemFile(HWItemDTO hwItem, File file) {
		return file.delete();
	}

	@Override
	public HWItemTypeDTO getHWItemType(Long fixTypeId) {
		return hwMapper.mapHWItemType(hwItemTypeRepository.findOne(fixTypeId));
	}

	@Override
	public long countHWItems(HWFilterDTO filter) {
		return hwItemRepository.countHWItems(filter);
	}

	@Override
	public List<HWItemOverviewDTO> getHWItems(HWFilterDTO filter, Pageable pageable, OrderSpecifier<?>[] order) {
		return hwMapper.mapHWItems(hwItemRepository.getHWItems(filter, pageable, order));
	}
}
