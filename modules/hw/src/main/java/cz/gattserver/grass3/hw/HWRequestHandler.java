package cz.gattserver.grass3.hw;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

import javax.servlet.annotation.WebServlet;

import org.springframework.beans.factory.annotation.Autowired;

import cz.gattserver.grass3.exception.GrassPageException;
import cz.gattserver.grass3.hw.service.HWService;
import cz.gattserver.grass3.server.AbstractConfiguratedPathRequestHandler;
import cz.gattserver.web.common.spring.SpringContextHelper;

@WebServlet(urlPatterns = "/" + HWConfiguration.HW_PATH + "/*")
public class HWRequestHandler extends AbstractConfiguratedPathRequestHandler {

	private static final long serialVersionUID = 7154339775034959876L;

	@Autowired
	private HWService hwService;

	public HWRequestHandler() {
		SpringContextHelper.inject(this);
	}

	@Override
	protected Path getPath(String fileName) throws FileNotFoundException {
		if (!fileName.matches("/[0-9]+/(icon|img|doc)/[^/]+"))
			throw new GrassPageException(400);
		String[] chunks = fileName.split("/");
		Long id = Long.parseLong(chunks[1]);
		String type = chunks[2];
		String name = chunks[3];
		if ("img".equals(type))
			return hwService.getHWItemImagesFilePath(id, name);
		if ("icon".equals(type))
			try {
				return hwService.getHWItemIconFile(id);
			} catch (IOException e) {
				throw new GrassPageException(404, "Nezdařilo se načíst požadovaný soubor");
			}
		return hwService.getHWItemDocumentsFilePath(id, name);
	}

	@Override
	protected String getMimeType(Path file) {
		String type = super.getMimeType(file);
		return type + "; charset=utf-8";
	}

}
