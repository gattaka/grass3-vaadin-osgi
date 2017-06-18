package cz.gattserver.grass3.rest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import cz.gattserver.grass3.pg.dto.PhotogalleryDTO;
import cz.gattserver.grass3.pg.dto.PhotogalleryRESTOverviewDTO;
import cz.gattserver.grass3.pg.facade.IPhotogalleryFacade;

@Controller
@RequestMapping("/pg")
public class PhotogalleryResource {

	@Autowired
	private IPhotogalleryFacade photogalleryFacade;

	@Resource(name = "securityFacade")
	private ISecurityFacade securityFacade;

	// $ curl -u gatt:tigris http://localhost:8180/web/ws/pg/log
	@RequestMapping("/log")
	public @ResponseBody String log(Principal principal) {
		if (principal != null) {
			String userId = principal.getName();
			return userId;
		} else {
			return "unauth";
		}
	}

	// curl -i -X POST -d login=gatt -d password=tigris
	// http://localhost:8180/web/ws/pg/login
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<String> login(@RequestParam("login") String username,
			@RequestParam("password") String password) {
		if (securityFacade.login(username, password)) {
			return new ResponseEntity<String>(HttpStatus.OK);
		} else {
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping("/list")
	public @ResponseBody List<PhotogalleryRESTOverviewDTO> list(Principal principal) {
		return photogalleryFacade.getAllPhotogalleriesForREST();
	}

	@RequestMapping("/gallery")
	public @ResponseBody PhotogalleryDTO gallery(@RequestParam(value = "id", required = true) Long id) {
		return photogalleryFacade.getPhotogalleryForDetail(id);
	}

	@RequestMapping("/photo")
	public void photo(@RequestParam(value = "id", required = true) Long id, String fileName,
			HttpServletResponse response) {
		try {
			// get your file as InputStream
			InputStream is = new FileInputStream(photogalleryFacade.getPhotoForREST(id, fileName));
			// copy it to response's OutputStream
			IOUtils.copy(is, response.getOutputStream());
			response.flushBuffer();
		} catch (IOException ex) {
			throw new RuntimeException("IOError writing file to output stream");
		}
	}

}
