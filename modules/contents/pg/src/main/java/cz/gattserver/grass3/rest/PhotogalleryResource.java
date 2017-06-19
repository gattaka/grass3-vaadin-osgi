package cz.gattserver.grass3.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cz.gattserver.grass3.facades.ISecurityFacade;
import cz.gattserver.grass3.model.dto.UserInfoDTO;
import cz.gattserver.grass3.pg.dto.PhotogalleryRESTDTO;
import cz.gattserver.grass3.pg.dto.PhotogalleryRESTOverviewDTO;
import cz.gattserver.grass3.pg.facade.IPhotogalleryFacade;
import cz.gattserver.grass3.pg.facade.UnauthorizedAccessException;

@Controller
@RequestMapping("/pg")
public class PhotogalleryResource {

	@Autowired
	private IPhotogalleryFacade photogalleryFacade;

	@Resource(name = "securityFacade")
	private ISecurityFacade securityFacade;

	@RequestMapping("/log")
	public @ResponseBody String log() {
		UserInfoDTO user = securityFacade.getCurrentUser();
		return user.getName() == null ? "unauth" : user.getName();
	}

	// curl -i -X POST -d login=jmeno -d password=heslo
	// http://localhost:8180/web/ws/pg/login
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<String> login(@RequestParam("login") String username,
			@RequestParam("password") String password) {
		if (securityFacade.login(username, password)) {
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	// http://localhost:8180/web/ws/pg/list
	@RequestMapping("/list")
	public @ResponseBody List<PhotogalleryRESTOverviewDTO> list() {
		UserInfoDTO user = securityFacade.getCurrentUser();
		return photogalleryFacade.getAllPhotogalleriesForREST(user.getId());
	}

	// http://localhost:8180/web/ws/pg/gallery?id=364
	@RequestMapping("/gallery")
	public ResponseEntity<PhotogalleryRESTDTO> gallery(@RequestParam(value = "id", required = true) Long id) {
		PhotogalleryRESTDTO gallery;
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
		try {
			File file = photogalleryFacade.getPhotoForREST(id, fileName, mini);
			if (file == null) {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				return;
			}
			InputStream is = new FileInputStream(file);
			IOUtils.copy(is, response.getOutputStream());
			response.flushBuffer();
		} catch (UnauthorizedAccessException e) {
			response.setStatus(HttpStatus.FORBIDDEN.value());
		} catch (IOException ex) {
			System.err.println("IOError writing file to output stream");
			ex.printStackTrace();
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
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

}
