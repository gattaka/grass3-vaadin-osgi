package cz.gattserver.grass3.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import cz.gattserver.grass3.events.EventBus;
import cz.gattserver.grass3.interfaces.UserInfoTO;
import cz.gattserver.grass3.pg.events.impl.PGProcessResultEvent;
import cz.gattserver.grass3.pg.exception.UnauthorizedAccessException;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryRESTTO;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryTO;
import cz.gattserver.grass3.pg.interfaces.PhotoVersion;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryPayloadTO;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryRESTOverviewTO;
import cz.gattserver.grass3.pg.service.PGService;
import cz.gattserver.grass3.services.SecurityService;

@Controller
@RequestMapping("/pg")
public class PGResource {

	private static Logger logger = LoggerFactory.getLogger(PGResource.class);

	@Autowired
	private PGService photogalleryFacade;

	@Autowired
	private SecurityService securityFacade;

	@Autowired
	private EventBus eventBus;

	// http://localhost:8180/web/ws/pg/count
	@RequestMapping("/count")
	public ResponseEntity<Integer> count(@RequestParam(value = "filter", required = false) String filter) {
		UserInfoTO user = securityFacade.getCurrentUser();
		return new ResponseEntity<>(photogalleryFacade.countAllPhotogalleriesForREST(user.getId(), filter),
				HttpStatus.OK);
	}

	// http://localhost:8180/web/ws/pg/list?page=1&pageSize=10
	@RequestMapping("/list")
	public ResponseEntity<List<PhotogalleryRESTOverviewTO>> list(
			@RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "pageSize", required = true) int pageSize,
			@RequestParam(value = "filter", required = false) String filter) {
		UserInfoTO user = securityFacade.getCurrentUser();
		int count = photogalleryFacade.countAllPhotogalleriesForREST(user.getId(), filter);
		// startIndex nesmí být víc než je počet, endIndex může být s tím si JPA
		// poradí a sníží ho
		if (page * pageSize > count)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(
				photogalleryFacade.getAllPhotogalleriesForREST(user.getId(), filter, new PageRequest(page, pageSize)),
				HttpStatus.OK);
	}

	// http://localhost:8180/web/ws/pg/gallery?id=364
	@RequestMapping("/gallery")
	public ResponseEntity<PhotogalleryRESTTO> gallery(@RequestParam(value = "id", required = true) Long id) {
		PhotogalleryRESTTO gallery;
		try {
			gallery = photogalleryFacade.getPhotogalleryForREST(id);
		} catch (UnauthorizedAccessException e) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		if (gallery == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		return new ResponseEntity<>(gallery, HttpStatus.OK);
	}

	private void innerPhoto(Long id, String fileName, PhotoVersion photoVersion, HttpServletResponse response) {
		Path file;
		try {
			file = photogalleryFacade.getPhotoForREST(id, fileName, photoVersion);
		} catch (UnauthorizedAccessException e) {
			response.setStatus(HttpStatus.FORBIDDEN.value());
			return;
		}

		if (file == null) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return;
		}

		try (InputStream is = new FileInputStream(file.toFile())) {
			IOUtils.copy(is, response.getOutputStream());
			response.setStatus(HttpStatus.OK.value());
			response.flushBuffer();
		} catch (IOException e) {
			logger.error("Nezdařilo se zapsat obsah souboru na výstup", e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			return;
		}
	}

	// http://localhost:8180/web/ws/pg/photo?id=364&fileName=shocked_kittens_cr.jpg
	@RequestMapping("/photo")
	public void photo(@RequestParam(value = "id", required = true) Long id, String fileName,
			HttpServletResponse response) {
		innerPhoto(id, fileName, PhotoVersion.FULL, response);
	}

	// http://localhost:8180/web/ws/pg/slideshow?id=364&fileName=shocked_kittens_cr.jpg
	@RequestMapping("/slideshow")
	public void slideshow(@RequestParam(value = "id", required = true) Long id, String fileName,
			HttpServletResponse response) {
		innerPhoto(id, fileName, PhotoVersion.SLIDESHOW, response);
	}

	// http://localhost:8180/web/ws/pg/mini?id=364&fileName=shocked_kittens_cr.jpg
	@RequestMapping("/mini")
	public void mini(@RequestParam(value = "id", required = true) Long id, String fileName,
			HttpServletResponse response) {
		innerPhoto(id, fileName, PhotoVersion.MINI, response);
	}

	// http://localhost:8180/web/ws/pg/create
	// https://resttesttest.com/
	// POST http://localhost:8180/web/ws/pg/create
	// galleryName test-gallery
	// files file1
	// files file2
	// files ...
	@RequestMapping(value = "/createfast", method = RequestMethod.POST)
	public ResponseEntity<String> createfast(@RequestParam(value = "galleryName", required = true) String galleryName,
			@RequestParam(value = "files", required = true) MultipartFile[] uploadingFiles)
			throws IllegalStateException, IOException {
		logger.info("/createfast volán");
		UserInfoTO user = securityFacade.getCurrentUser();
		if (user == null)
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		String galleryDir = null;
		try {
			galleryDir = photogalleryFacade.createGalleryDir();
			for (MultipartFile uploadedFile : uploadingFiles) {
				logger.info("/createfast zpracován soubor " + uploadedFile.getOriginalFilename());
				photogalleryFacade.uploadFile(uploadedFile.getInputStream(), uploadedFile.getOriginalFilename(),
						galleryDir);
			}

			UUID operationId = UUID.randomUUID();

			PhotogalleryPayloadTO payloadTO = new PhotogalleryPayloadTO(galleryName, galleryDir, null, true, false);
			photogalleryFacade.savePhotogallery(operationId, payloadTO, 55L, 1L, LocalDateTime.now());

			logger.info("/createfast dokončen");
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			logger.error("/createfast chyba", e);
			if (galleryDir != null) {
				try {
					new File(galleryDir).delete();
				} catch (Exception ee) {
					logger.error("/createfast nezdařilo se smazat adresář galerie, u které došlo k chybě", ee);
				}
			}
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<Long> create(@RequestParam(value = "galleryName", required = true) String galleryName)
			throws IllegalStateException, IOException {
		UserInfoTO user = securityFacade.getCurrentUser();
		if (user == null)
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		logger.info("/create volán");
		try {
			String galleryDir = photogalleryFacade.createGalleryDir();

			UUID operationId = UUID.randomUUID();

			PGEventsHandler eventsHandler = new PGEventsHandler();
			eventBus.subscribe(eventsHandler);
			CompletableFuture<PGEventsHandler> future = eventsHandler.expectEvent(operationId);

			PhotogalleryPayloadTO payloadTO = new PhotogalleryPayloadTO(galleryName, galleryDir, null, false, false);
			photogalleryFacade.savePhotogallery(operationId, payloadTO, 55L, user.getId(), LocalDateTime.now());

			eventsHandler = future.get();
			PGProcessResultEvent event = eventsHandler.getResultAndDelete(operationId);
			if (event.isSuccess()) {
				logger.info("/create chyba", event.getResultDetails());
				return new ResponseEntity<>(event.getGalleryId(), HttpStatus.INTERNAL_SERVER_ERROR);
			}

			logger.info("/create dokončen");
			return new ResponseEntity<>(event.getGalleryId(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("/upload chyba", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public ResponseEntity<String> create(@RequestParam(value = "galleryId", required = true) Long galleryId,
			@RequestParam(value = "file", required = true) MultipartFile uploadedFile)
			throws IllegalStateException, IOException {
		UserInfoTO user = securityFacade.getCurrentUser();
		if (user == null)
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		logger.info("/upload volán");
		try {
			PhotogalleryTO to = photogalleryFacade.getPhotogalleryForDetail(galleryId);
			photogalleryFacade.uploadFile(uploadedFile.getInputStream(), uploadedFile.getOriginalFilename(),
					to.getPhotogalleryPath());

			logger.info("/upload dokončen");
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			logger.error("/upload chyba", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/process", method = RequestMethod.POST)
	public ResponseEntity<String> process(@RequestParam(value = "galleryId", required = true) Long galleryId)
			throws IllegalStateException, IOException {
		UserInfoTO user = securityFacade.getCurrentUser();
		if (user == null)
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		logger.info("/process volán");
		try {
			UUID operationId = UUID.randomUUID();

			PGEventsHandler eventsHandler = new PGEventsHandler();
			eventBus.subscribe(eventsHandler);
			CompletableFuture<PGEventsHandler> future = eventsHandler.expectEvent(operationId);

			PhotogalleryTO to = photogalleryFacade.getPhotogalleryForDetail(galleryId);
			PhotogalleryPayloadTO payloadTO = new PhotogalleryPayloadTO(to.getContentNode().getName(),
					to.getPhotogalleryPath(), to.getContentNode().getContentTagsAsStrings(),
					to.getContentNode().isPublicated(), true);
			photogalleryFacade.modifyPhotogallery(operationId, to.getId(), payloadTO, LocalDateTime.now());

			eventsHandler = future.get();
			PGProcessResultEvent event = eventsHandler.getResultAndDelete(operationId);
			if (event.isSuccess()) {
				logger.info("/create chyba", event.getResultDetails());
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}

			logger.info("/process dokončen");
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			logger.error("/process chyba", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
