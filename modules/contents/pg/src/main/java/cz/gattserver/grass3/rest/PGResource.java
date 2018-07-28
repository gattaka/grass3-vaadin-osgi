package cz.gattserver.grass3.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cz.gattserver.grass3.interfaces.UserInfoTO;
import cz.gattserver.grass3.pg.exception.UnauthorizedAccessException;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryRESTTO;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryPayloadTO;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryRESTOverviewTO;
import cz.gattserver.grass3.pg.service.PGService;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.services.impl.LoginResult;

@Controller
@RequestMapping("/pg")
public class PGResource {

	private static Logger logger = LoggerFactory.getLogger(PGResource.class);

	@Autowired
	private PGService photogalleryFacade;

	@Autowired
	private SecurityService securityFacade;

	@RequestMapping("/log")
	public @ResponseBody String log() {
		UserInfoTO user = securityFacade.getCurrentUser();
		return user.getName() == null ? "unauth" : user.getName();
	}

	// curl -i -X POST -d login=jmeno -d password=heslo
	// http://localhost:8180/web/ws/pg/login
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<String> login(@RequestParam("login") String username,
			@RequestParam("password") String password, HttpServletRequest request, HttpServletResponse response) {
		LoginResult result = securityFacade.login(username, password, false, request, response);
		if (LoginResult.SUCCESS.equals(result)) {
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	// http://localhost:8180/web/ws/pg/count
	@RequestMapping("/count")
	public ResponseEntity<Integer> count() {
		UserInfoTO user = securityFacade.getCurrentUser();
		return new ResponseEntity<>(photogalleryFacade.countAllPhotogalleriesForREST(user.getId()), HttpStatus.OK);
	}

	// http://localhost:8180/web/ws/pg/list?page=1&pageSize=10
	@RequestMapping("/list")
	public ResponseEntity<List<PhotogalleryRESTOverviewTO>> list(
			@RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "pageSize", required = true) int pageSize) {
		UserInfoTO user = securityFacade.getCurrentUser();
		int count = photogalleryFacade.countAllPhotogalleriesForREST(user.getId());
		// startIndex nesmí být víc než je počet, endIndex může být s tím si JPA
		// poradí a sníží ho
		if (page * pageSize > count)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(photogalleryFacade.getAllPhotogalleriesForREST(user.getId(), page, pageSize),
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

	private void innerPhoto(Long id, String fileName, boolean mini, HttpServletResponse response) {
		Path file;
		try {
			file = photogalleryFacade.getPhotoForREST(id, fileName, mini);
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
		innerPhoto(id, fileName, false, response);
	}

	// http://localhost:8180/web/ws/pg/mini?id=364&fileName=shocked_kittens_cr.jpg
	@RequestMapping("/mini")
	public void mini(@RequestParam(value = "id", required = true) Long id, String fileName,
			HttpServletResponse response) {
		innerPhoto(id, fileName, true, response);
	}

	// http://localhost:8180/web/ws/pg/create
	// https://resttesttest.com/
	// POST http://localhost:8180/web/ws/pg/create
	// galleryName test-gallery
	// files file1
	// files file2
	// files ...
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<String> handleFileUpload(
			@RequestParam(value = "galleryName", required = true) String galleryName,
			@RequestParam(value = "files", required = true) MultipartFile[] uploadingFiles)
			throws IllegalStateException, IOException {
		logger.info("handleFileUpload /create volán");
		String galleryDir = photogalleryFacade.createGalleryDir();
		try {
			for (MultipartFile uploadedFile : uploadingFiles) {
				logger.info("handleFileUpload /create zpracován soubor " + uploadedFile.getOriginalFilename());
				photogalleryFacade.uploadFile(uploadedFile.getInputStream(), uploadedFile.getOriginalFilename(),
						galleryDir);
			}
			PhotogalleryPayloadTO payloadTO = new PhotogalleryPayloadTO(galleryName, galleryDir, null, true, false);
			photogalleryFacade.savePhotogallery(payloadTO, 55L, 1L, LocalDateTime.now());
		} catch (Exception e) {
			logger.error("handleFileUpload /create chyba", e);
			new File(galleryDir).delete();
		}

		logger.info("handleFileUpload /create dokončen");
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
