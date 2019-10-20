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
import java.util.stream.Stream;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.OrderSpecifier;

import cz.gattserver.common.util.DateUtils;
import cz.gattserver.common.util.HumanBytesSizeFormatter;
import cz.gattserver.grass3.exception.GrassException;
import cz.gattserver.grass3.hw.HWConfiguration;
import cz.gattserver.grass3.hw.interfaces.HWFilterTO;
import cz.gattserver.grass3.hw.interfaces.HWItemTO;
import cz.gattserver.grass3.hw.interfaces.HWItemFileTO;
import cz.gattserver.grass3.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass3.hw.interfaces.HWItemTypeTO;
import cz.gattserver.grass3.hw.interfaces.ServiceNoteTO;
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

	private static final String ILLEGAL_PATH_IMGS_ERR = "Podtečení adresáře grafických příloh";
	private static final String ILLEGAL_PATH_DOCS_ERR = "Podtečení adresáře dokumentací";

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
	public void saveImagesFile(InputStream in, String fileName, HWItemTO item) throws IOException {
		Path imagesPath;
		imagesPath = getHWItemImagesPath(item.getId());
		Path imagePath = imagesPath.resolve(fileName);
		if (!imagePath.normalize().startsWith(imagesPath))
			throw new IllegalArgumentException(ILLEGAL_PATH_IMGS_ERR);
		Files.copy(in, imagePath, StandardCopyOption.REPLACE_EXISTING);
	}

	@Override
	public List<HWItemFileTO> getHWItemImagesFiles(Long id) {
		Path imagesPath;
		try {
			imagesPath = getHWItemImagesPath(id);
			List<HWItemFileTO> list = new ArrayList<>();
			try (Stream<Path> stream = Files.list(imagesPath)) {
				stream.forEach(p -> list.add(mapPathToItem(p)));
			}
			return list;
		} catch (IOException e) {
			throw new GrassException("Nezdařilo se získat přehled grafických příloh HW položky.", e);
		}
	}

	@Override
	public long getHWItemImagesFilesCount(Long id) {
		Path imagesPath;
		try {
			imagesPath = getHWItemImagesPath(id);
			try (Stream<Path> stream = Files.list(imagesPath)) {
				return stream.count();
			}
		} catch (IOException e) {
			throw new GrassException("Nezdařilo se získat přehled grafických příloh HW položky.", e);
		}
	}

	@Override
	public Path getHWItemImagesFilePath(Long id, String name) {
		Path images;
		try {
			images = getHWItemImagesPath(id);
			Path image = images.resolve(name);
			if (!image.normalize().startsWith(images))
				throw new IllegalArgumentException(ILLEGAL_PATH_IMGS_ERR);
			return image;
		} catch (IOException e) {
			throw new GrassException("Nezdařilo se získat grafickou přílohu HW položky.", e);
		}
	}

	@Override
	public InputStream getHWItemImagesFileInputStream(Long id, String name) {
		try {
			return Files.newInputStream(getHWItemImagesFilePath(id, name));
		} catch (IOException e) {
			throw new GrassException("Nezdařilo se získat grafickou přílohu HW položky.", e);
		}
	}

	@Override
	public boolean deleteHWItemImagesFile(Long id, String name) {
		Path images;
		try {
			images = getHWItemImagesPath(id);
			Path image = images.resolve(name);
			if (!image.normalize().startsWith(images))
				throw new IllegalArgumentException(ILLEGAL_PATH_IMGS_ERR);
			return Files.deleteIfExists(image);
		} catch (IOException e) {
			throw new GrassException("Nezdařilo se smazat grafickou přílohu HW položky.", e);
		}
	}

	/*
	 * Documents
	 */

	@Override
	public void saveDocumentsFile(InputStream in, String fileName, Long id) throws IOException {
		Path docsPath = getHWItemDocumentsPath(id);
		Path docPath = docsPath.resolve(fileName);
		if (!docPath.normalize().startsWith(docsPath))
			throw new IllegalArgumentException(ILLEGAL_PATH_DOCS_ERR);
		Files.copy(in, docPath, StandardCopyOption.REPLACE_EXISTING);
	}

	@Override
	public List<HWItemFileTO> getHWItemDocumentsFiles(Long id) {
		Path docsPath;
		try {
			docsPath = getHWItemDocumentsPath(id);
			List<HWItemFileTO> list = new ArrayList<>();
			try (Stream<Path> stream = Files.list(docsPath)) {
				stream.forEach(p -> list.add(mapPathToItem(p)));
			}
			return list;
		} catch (IOException e) {
			throw new GrassException("Nezdařilo se získat přehled dokumentací HW položky", e);
		}
	}

	@Override
	public long getHWItemDocumentsFilesCount(Long id) {
		Path docsPath;
		try {
			docsPath = getHWItemDocumentsPath(id);
			try (Stream<Path> stream = Files.list(docsPath)) {
				return stream.count();
			}
		} catch (IOException e) {
			throw new GrassException("Nezdařilo se získat přehled dokumentací HW položky", e);
		}
	}

	@Override
	public Path getHWItemDocumentsFilePath(Long id, String name) {
		Path docs;
		try {
			docs = getHWItemDocumentsPath(id);
			Path doc = docs.resolve(name);
			if (!doc.normalize().startsWith(docs))
				throw new IllegalArgumentException(ILLEGAL_PATH_DOCS_ERR);
			return doc;
		} catch (IOException e) {
			throw new GrassException("Nezdařilo se získat soubor dokumentace HW položky", e);
		}
	}

	@Override
	public InputStream getHWItemDocumentsFileInputStream(Long id, String name) {
		try {
			return Files.newInputStream(getHWItemDocumentsFilePath(id, name));
		} catch (IOException e) {
			throw new GrassException("Nezdařilo se získat soubor dokumentace HW položky", e);
		}
	}

	@Override
	public boolean deleteHWItemDocumentsFile(Long id, String name) {
		Path docs;
		try {
			docs = getHWItemDocumentsPath(id);
			Path doc = docs.resolve(name);
			if (!doc.normalize().startsWith(docs))
				throw new IllegalArgumentException(ILLEGAL_PATH_DOCS_ERR);
			return Files.deleteIfExists(doc);
		} catch (IOException e) {
			throw new GrassException("Nezdařilo se smazat soubor dokumentace HW položky.", e);
		}
	}

	/*
	 * Icons
	 */

	@Override
	public OutputStream createHWItemIconOutputStream(String filename, Long id) {
		String[] parts = filename.split("\\.");
		String extension = parts.length >= 1 ? parts[parts.length - 1] : "";

		Path hwItemDir;
		try {
			hwItemDir = getHWPath(id);
			Path file = hwItemDir.resolve("icon." + extension);
			return Files.newOutputStream(file);
		} catch (IOException e) {
			throw new GrassException("Nezdařila se příprava pro uložení ikony HW položky.", e);
		}
	}

	private Path getHWItemIconFile(Long id) throws IOException {
		Path hwPath = getHWPath(id);
		if (!Files.exists(hwPath))
			return null;
		try (Stream<Path> stream = Files.list(hwPath)) {
			return stream.filter(p -> p.getFileName().toString().matches("icon\\.[^\\.]*")).findFirst().orElse(null);
		}
	}

	@Override
	public InputStream getHWItemIconFileInputStream(Long id) {
		Path path;
		try {
			path = getHWItemIconFile(id);
			return path != null ? Files.newInputStream(path) : null;
		} catch (IOException e) {
			throw new GrassException("Nezdařilo se získat ikonu HW položky.", e);
		}
	}

	@Override
	public boolean deleteHWItemIconFile(Long id) {
		Path image;
		try {
			image = getHWItemIconFile(id);
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
	public Long saveHWType(HWItemTypeTO hwItemTypeTO) {
		HWItemType type = hwMapper.mapHWItem(hwItemTypeTO);
		type = hwItemTypeRepository.save(type);
		return type.getId();
	}

	@Override
	public Set<HWItemTypeTO> getAllHWTypes() {
		List<HWItemType> hwItemTypes = hwItemTypeRepository.findListOrderByName();
		return hwMapper.mapHWItemTypes(hwItemTypes);
	}

	@Override
	public HWItemTypeTO getHWItemType(Long fixTypeId) {
		return hwMapper.mapHWItemType(hwItemTypeRepository.findById(fixTypeId).orElse(null));
	}

	@Override
	public void deleteHWItemType(Long id) {
		HWItemType itemType = hwItemTypeRepository.findById(id).orElse(null);
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
	public Long saveHWItem(HWItemTO hwItemDTO) {
		HWItem item;
		if (hwItemDTO.getId() == null)
			item = new HWItem();
		else
			item = hwItemRepository.findById(hwItemDTO.getId()).orElse(null);
		item.setName(hwItemDTO.getName());
		item.setPurchaseDate(DateUtils.toDate(hwItemDTO.getPurchaseDate()));
		item.setDestructionDate(DateUtils.toDate(hwItemDTO.getDestructionDate()));
		item.setPrice(hwItemDTO.getPrice());
		item.setState(hwItemDTO.getState());
		item.setSupervizedFor(hwItemDTO.getSupervizedFor());
		if (hwItemDTO.getUsedIn() != null) {
			HWItem usedIn = hwItemRepository.findById(hwItemDTO.getUsedIn().getId()).orElse(null);
			item.setUsedIn(usedIn);
		}
		item.setWarrantyYears(hwItemDTO.getWarrantyYears());
		if (hwItemDTO.getTypes() != null) {
			item.setTypes(new HashSet<HWItemType>());
			for (String typeName : hwItemDTO.getTypes()) {
				HWItemType type = hwItemTypeRepository.findByName(typeName);
				if (type == null) {
					type = new HWItemType(typeName);
					type = hwItemTypeRepository.save(type);
				}
				item.getTypes().add(type);
			}
		}
		return hwItemRepository.save(item).getId();
	}

	@Override
	public int countHWItems(HWFilterTO filter) {
		return (int) hwItemRepository.countHWItems(filter);
	}

	@Override
	public List<HWItemOverviewTO> getAllHWItems() {
		List<HWItem> hwItemTypes = hwItemRepository.findAll();
		return hwMapper.mapHWItems(hwItemTypes);
	}

	@Override
	public List<HWItemOverviewTO> getHWItems(HWFilterTO filter, int offset, int limit, OrderSpecifier<?>[] order) {
		return hwMapper.mapHWItems(hwItemRepository.getHWItems(filter, offset, limit, order));
	}

	@Override
	public List<HWItemOverviewTO> getHWItemsByTypes(Collection<String> types) {
		List<HWItem> hwItemTypes = hwItemRepository.getHWItemsByTypes(types);
		return hwMapper.mapHWItems(hwItemTypes);
	}

	@Override
	public HWItemTO getHWItem(Long itemId) {
		return hwMapper.mapHWItem(hwItemRepository.findById(itemId).orElse(null));
	}

	@Override
	public List<HWItemOverviewTO> getAllParts(Long usedInItemId) {
		return hwMapper.mapHWItems(hwItemRepository.findByUsedInId(usedInItemId));
	}

	@Override
	public List<HWItemOverviewTO> getHWItemsAvailableForPart(Long itemId) {
		List<HWItem> hwItemTypes = hwItemRepository.findAllExcept(itemId);
		return hwMapper.mapHWItems(hwItemTypes);
	}

	@Override
	public void deleteHWItem(Long id) {
		HWItem item = hwItemRepository.findById(id).orElse(null);
		for (ServiceNote note : item.getServiceNotes()) {
			serviceNoteRepository.delete(note);
		}

		item.setServiceNotes(null);
		hwItemRepository.save(item);

		for (HWItem targetItem : hwItemRepository.findByUsedInId(item.getId())) {
			targetItem.setUsedIn(null);
			hwItemRepository.save(targetItem);
		}

		hwItemRepository.deleteById(item.getId());
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
		HWItem targetItem = hwItemRepository.findById(triggerItem.getUsedIn().getId()).orElse(null);
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
	public void addServiceNote(ServiceNoteTO serviceNoteDTO, Long id) {
		HWItem item = hwItemRepository.findById(id).orElse(null);
		ServiceNote serviceNote = new ServiceNote();
		serviceNote.setDate(DateUtils.toDate(serviceNoteDTO.getDate()));
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
			HWItem targetItem = hwItemRepository.findById(serviceNoteDTO.getUsedInId()).orElse(null);

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
	public void modifyServiceNote(ServiceNoteTO serviceNoteDTO) {
		ServiceNote serviceNote = serviceNoteRepository.findById(serviceNoteDTO.getId()).orElse(null);
		serviceNote.setDate(DateUtils.toDate(serviceNoteDTO.getDate()));
		serviceNote.setDescription(serviceNoteDTO.getDescription());
		serviceNote.setState(serviceNoteDTO.getState());
		serviceNote.setUsage(serviceNoteDTO.getUsedInName());
		serviceNoteRepository.save(serviceNote);
	}

	@Override
	public void deleteServiceNote(ServiceNoteTO serviceNoteDTO, Long id) {
		HWItem item = hwItemRepository.findById(id).orElse(null);
		ServiceNote serviceNote = serviceNoteRepository.findById(serviceNoteDTO.getId()).orElse(null);
		item.getServiceNotes().remove(serviceNote);
		hwItemRepository.save(item);
		serviceNoteRepository.delete(serviceNote);
	}

}
