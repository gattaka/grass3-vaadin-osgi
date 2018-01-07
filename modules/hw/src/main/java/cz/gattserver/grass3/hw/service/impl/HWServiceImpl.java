package cz.gattserver.grass3.hw.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.OrderSpecifier;

import cz.gattserver.common.util.DateUtil;
import cz.gattserver.common.util.HumanBytesSizeFormatter;
import cz.gattserver.grass3.exception.GrassException;
import cz.gattserver.grass3.hw.HWConfiguration;
import cz.gattserver.grass3.hw.interfaces.HWFilterDTO;
import cz.gattserver.grass3.hw.interfaces.HWItemDTO;
import cz.gattserver.grass3.hw.interfaces.HWItemFileTO;
import cz.gattserver.grass3.hw.interfaces.HWItemOverviewDTO;
import cz.gattserver.grass3.hw.interfaces.HWItemTypeDTO;
import cz.gattserver.grass3.hw.interfaces.ServiceNoteDTO;
import cz.gattserver.grass3.hw.model.domain.HWItem;
import cz.gattserver.grass3.hw.model.domain.HWItemType;
import cz.gattserver.grass3.hw.model.domain.ServiceNote;
import cz.gattserver.grass3.hw.model.repositories.HWItemRepository;
import cz.gattserver.grass3.hw.model.repositories.HWItemTypeRepository;
import cz.gattserver.grass3.hw.model.repositories.ServiceNoteRepository;
import cz.gattserver.grass3.hw.service.HWMapperService;
import cz.gattserver.grass3.hw.service.HWService;
import cz.gattserver.grass3.services.ConfigurationService;
import cz.gattserver.grass3.services.FileSystemService;

@Transactional
@Component
public class HWServiceImpl implements HWService {

	private static final Logger logger = LoggerFactory.getLogger(HWServiceImpl.class);

	@Autowired
	private FileSystemService fileSystemService;

	@Autowired
	private HWItemRepository hwItemRepository;

	@Autowired
	private HWItemTypeRepository hwItemTypeRepository;

	@Autowired
	private ServiceNoteRepository serviceNoteRepository;

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private HWMapperService hwMapper;

	/*
	 * Config
	 */

	private HWConfiguration loadConfiguration() {
		HWConfiguration configuration = new HWConfiguration();
		configurationService.loadConfiguration(configuration);
		return configuration;
	}

	/**
	 * Získá {@link Path} dle jména adresáře HW položky
	 * 
	 * @param id
	 *            id HW položky
	 * @return {@link Path} adresář galerie
	 * @throws IllegalStateException
	 *             pokud neexistuje kořenový adresář HW -- chyba nastavení
	 *             modulu HW
	 * @throws IllegalArgumentException
	 *             pokud předaný adresář podtéká kořen modulu HW
	 */
	private Path getHWPath(Long id) {
		Validate.notNull(id, "ID HW položky nesmí být null");
		HWConfiguration configuration = loadConfiguration();
		String rootDir = configuration.getRootDir();
		Path rootPath = fileSystemService.getFileSystem().getPath(rootDir);
		if (!Files.exists(rootPath))
			throw new IllegalStateException("Kořenový adresář HW modulu musí existovat");
		rootPath = rootPath.normalize();
		Path hwPath = rootPath.resolve(String.valueOf(id));
		if (!hwPath.normalize().startsWith(rootPath))
			throw new IllegalArgumentException("Podtečení kořenového adresáře galerií");
		return hwPath;
	}

	private Path getHWItemDocumentsPath(Long id) throws IOException {
		HWConfiguration configuration = loadConfiguration();
		Path hwPath = getHWPath(id);
		Path file = hwPath.resolve(configuration.getDocumentsDir());
		if (!Files.exists(file))
			Files.createDirectories(file);
		return file;
	}

	private Path getHWItemImagesPath(Long id) throws IOException {
		HWConfiguration configuration = loadConfiguration();
		Path hwPath = getHWPath(id);
		Path file = hwPath.resolve(configuration.getImagesDir());
		if (!Files.exists(file))
			Files.createDirectories(file);
		return file;
	}

	private HWItemFileTO mapPathToItem(Path path) {
		HWItemFileTO to = new HWItemFileTO().setName(path.getFileName().toString());
		try {
			to.setSize(HumanBytesSizeFormatter.format(Files.size(path), true));
		} catch (IOException e) {
			to.setSize("n/a");
		}
		try {
			to.setLastModified(
					LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneId.systemDefault()));
		} catch (IOException e) {
			to.setLastModified(null);
		}
		return to;
	}

	/*
	 * Images
	 */

	@Override
	public boolean saveImagesFile(InputStream in, String fileName, HWItemDTO item) {
		Path imagesPath;
		try {
			imagesPath = getHWItemImagesPath(item.getId());
			Path imagePath = imagesPath.resolve(fileName);
			if (!imagePath.normalize().startsWith(imagesPath))
				throw new IllegalArgumentException("Podtečení adresáře grafických příloh");
			Files.copy(in, imagePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			logger.error("Nezdařilo se uložit obrázek k HW", e);
			return false;
		}
		return true;
	}

	@Override
	public List<HWItemFileTO> getHWItemImagesFiles(HWItemDTO itemDTO) {
		Path imagesPath;
		try {
			imagesPath = getHWItemImagesPath(itemDTO.getId());
			List<HWItemFileTO> list = new ArrayList<>();
			Files.list(imagesPath).forEach(p -> list.add(mapPathToItem(p)));
			return list;
		} catch (IOException e) {
			throw new GrassException("Nezdařilo se získat přehled grafických příloh HW položky.", e);
		}
	}

	@Override
	public InputStream getHWItemImagesFileInputStream(HWItemDTO hwItem, String name) {
		Path images;
		try {
			images = getHWItemImagesPath(hwItem.getId());
			Path image = images.resolve(name);
			if (!image.normalize().startsWith(images))
				throw new IllegalArgumentException("Podtečení adresáře grafických příloh");
			return Files.newInputStream(image);
		} catch (IOException e) {
			throw new GrassException("Nezdařilo se získat grafickou přílohu HW položky.", e);
		}
	}

	@Override
	public boolean deleteHWItemImagesFile(HWItemDTO hwItem, String name) {
		Path images;
		try {
			images = getHWItemImagesPath(hwItem.getId());
			Path image = images.resolve(name);
			if (!image.normalize().startsWith(images))
				throw new IllegalArgumentException("Podtečení adresáře grafických příloh");
			return Files.deleteIfExists(image);
		} catch (IOException e) {
			throw new GrassException("Nezdařilo se smazat grafickou přílohu HW položky.", e);
		}
	}

	/*
	 * Documents
	 */

	@Override
	public boolean saveDocumentsFile(InputStream in, String fileName, HWItemDTO item) {
		try {
			Path docsPath = getHWItemDocumentsPath(item.getId());
			Path docPath = docsPath.resolve(fileName);
			if (!docPath.normalize().startsWith(docsPath))
				throw new IllegalArgumentException("Podtečení adresáře dokumentací");
			Files.copy(in, docPath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new GrassException("Nezdařilo se uložit dokument k HW", e);
		}
		return true;
	}

	@Override
	public List<HWItemFileTO> getHWItemDocumentsFiles(HWItemDTO itemDTO) {
		Path docsPath;
		try {
			docsPath = getHWItemDocumentsPath(itemDTO.getId());
			List<HWItemFileTO> list = new ArrayList<>();
			Files.list(docsPath).forEach(p -> list.add(mapPathToItem(p)));
			return list;
		} catch (IOException e) {
			throw new GrassException("Nezdařilo se získat přehled dokumentací HW položky", e);
		}
	}

	@Override
	public InputStream getHWItemDocumentsFileInputStream(HWItemDTO hwItem, String name) {
		Path docs;
		try {
			docs = getHWItemDocumentsPath(hwItem.getId());
			Path doc = docs.resolve(name);
			if (!doc.normalize().startsWith(docs))
				throw new IllegalArgumentException("Podtečení adresáře dokumentací");
			return Files.newInputStream(doc);
		} catch (IOException e) {
			throw new GrassException("Nezdařilo se získat soubor dokumentace HW položky", e);
		}
	}

	@Override
	public boolean deleteHWItemDocumentsFile(HWItemDTO hwItem, String name) {
		Path docs;
		try {
			docs = getHWItemDocumentsPath(hwItem.getId());
			Path doc = docs.resolve(name);
			if (!doc.normalize().startsWith(docs))
				throw new IllegalArgumentException("Podtečení adresáře dokumentací");
			return Files.deleteIfExists(doc);
		} catch (IOException e) {
			throw new GrassException("Nezdařilo se smazat soubor dokumentace HW položky.", e);
		}
	}

	/*
	 * Icons
	 */

	@Override
	public OutputStream createHWItemIconOutputStream(String filename, HWItemDTO hwItem) {
		String[] parts = filename.split("\\.");
		String extension = parts.length >= 1 ? parts[parts.length - 1] : "";

		long time = System.currentTimeMillis();
		int uniqueHash = (String.valueOf(time) + hwItem.getName()).hashCode();

		Path hwItemDir;
		try {
			hwItemDir = getHWPath(hwItem.getId());
			Path file = hwItemDir.resolve("icon-" + uniqueHash + "." + extension);
			return Files.newOutputStream(file);
		} catch (IOException e) {
			throw new GrassException("Nezdařila se příprava pro uložení ikony HW položky.", e);
		}
	}

	private Path getHWItemIconFile(HWItemDTO hwItem) throws IOException {
		Path hwPath = getHWPath(hwItem.getId());
		// ikona HW je nějaký obrázek s názvem "icon-###.@@@",
		// kde ### je hash a @@@ je přípona
		return Files.list(hwPath).filter(p -> p.getFileName().toString().matches("icon-[^\\.]*\\.[^\\.]*")).findFirst()
				.orElse(null);
	}

	@Override
	public InputStream getHWItemIconFileInputStream(HWItemDTO hwItem) {
		Path path;
		try {
			path = getHWItemIconFile(hwItem);
			return path != null ? Files.newInputStream(path) : null;
		} catch (IOException e) {
			throw new GrassException("Nezdařilo se získat ikonu HW položky.", e);
		}
	}

	@Override
	public boolean deleteHWItemIconFile(HWItemDTO hwItem) {
		Path image;
		try {
			image = getHWItemIconFile(hwItem);
			if (image != null)
				return Files.deleteIfExists(image);
			return false;
		} catch (IOException e) {
			throw new GrassException("Nezdařilo se smazat ikonu HW položky.", e);
		}
	}

	/*
	 * Item types
	 */

	@Override
	public void saveHWType(HWItemTypeDTO hwItemTypeDTO) {
		HWItemType type = hwMapper.mapHWItem(hwItemTypeDTO);
		hwItemTypeRepository.save(type);
	}

	@Override
	public Set<HWItemTypeDTO> getAllHWTypes() {
		List<HWItemType> hwItemTypes = hwItemTypeRepository.findListOrderByName();
		return hwMapper.mapHWItemTypes(hwItemTypes);
	}

	@Override
	public HWItemTypeDTO getHWItemType(Long fixTypeId) {
		return hwMapper.mapHWItemType(hwItemTypeRepository.findOne(fixTypeId));
	}

	@Override
	public void deleteHWItemType(Long id) {
		HWItemType itemType = hwItemTypeRepository.findOne(id);
		List<HWItem> items = hwItemRepository.findByTypesId(itemType.getId());
		for (HWItem item : items) {
			item.getTypes().remove(itemType);
			hwItemRepository.save(item);
		}
		hwItemTypeRepository.delete(itemType);
	}

	/*
	 * Items
	 */

	@Override
	public void saveHWItem(HWItemDTO hwItemDTO) {
		HWItem item;
		if (hwItemDTO.getId() == null)
			item = new HWItem();
		else
			item = hwItemRepository.findOne(hwItemDTO.getId());
		item.setName(hwItemDTO.getName());
		item.setPurchaseDate(DateUtil.toDate(hwItemDTO.getPurchaseDate()));
		item.setDestructionDate(DateUtil.toDate(hwItemDTO.getDestructionDate()));
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
		hwItemRepository.save(item);
	}

	@Override
	public int countHWItems(HWFilterDTO filter) {
		return (int) hwItemRepository.countHWItems(filter);
	}

	@Override
	public List<HWItemOverviewDTO> getAllHWItems() {
		List<HWItem> hwItemTypes = hwItemRepository.findAll();
		return hwMapper.mapHWItems(hwItemTypes);
	}

	@Override
	public List<HWItemOverviewDTO> getHWItems(HWFilterDTO filter, Pageable pageable, OrderSpecifier<?>[] order) {
		return hwMapper.mapHWItems(hwItemRepository.getHWItems(filter, pageable, order));
	}

	@Override
	public List<HWItemOverviewDTO> getHWItemsByTypes(Collection<String> types) {
		List<HWItem> hwItemTypes = hwItemRepository.getHWItemsByTypes(types);
		return hwMapper.mapHWItems(hwItemTypes);
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
	public void deleteHWItem(Long id) {
		HWItem item = hwItemRepository.findOne(id);
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
	}

	/*
	 * Service notes
	 */

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
	public void addServiceNote(ServiceNoteDTO serviceNoteDTO, HWItemDTO hwItemDTO) {
		HWItem item = hwItemRepository.findOne(hwItemDTO.getId());
		ServiceNote serviceNote = new ServiceNote();
		serviceNote.setDate(DateUtil.toDate(serviceNoteDTO.getDate()));
		serviceNote.setDescription(serviceNoteDTO.getDescription());
		serviceNote.setState(serviceNoteDTO.getState());
		serviceNote.setUsage(serviceNoteDTO.getUsedInName());
		serviceNote = serviceNoteRepository.save(serviceNote);
		serviceNoteDTO.setId(serviceNote.getId());

		if (item.getServiceNotes() == null)
			item.setServiceNotes(new ArrayList<ServiceNote>());
		item.getServiceNotes().add(serviceNote);
		item.setState(serviceNote.getState());

		HWItem oldTarget = item.getUsedIn();

		// HW je někde součástí
		if (serviceNoteDTO.getUsedInId() != null) {

			// cílový HW, kde je nyní HW součástí
			HWItem targetItem = hwItemRepository.findOne(serviceNoteDTO.getUsedInId());

			// předtím nebyl nikde součástí
			if (oldTarget == null) {
				item.setUsedIn(targetItem);
				saveHWPartMoveServiceNote(item, serviceNote, true);
			} else if (oldTarget.getId() != serviceNoteDTO.getUsedInId()) {
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
	}

	@Override
	public void modifyServiceNote(ServiceNoteDTO serviceNoteDTO) {
		ServiceNote serviceNote = serviceNoteRepository.findOne(serviceNoteDTO.getId());
		serviceNote.setDate(DateUtil.toDate(serviceNoteDTO.getDate()));
		serviceNote.setDescription(serviceNoteDTO.getDescription());
		serviceNote.setState(serviceNoteDTO.getState());
		serviceNote.setUsage(serviceNoteDTO.getUsedInName());
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

}
