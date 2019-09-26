package cz.gattserver.grass3.campgames;

import java.io.FileNotFoundException;
import java.nio.file.Path;

import javax.servlet.annotation.WebServlet;

import org.springframework.beans.factory.annotation.Autowired;

import cz.gattserver.grass3.campgames.service.CampgamesService;
import cz.gattserver.grass3.server.AbstractConfiguratedPathRequestHandler;

@WebServlet(urlPatterns = "/" + CampgamesConfiguration.CAMPGAMES_PATH + "/*")
public class CampgamesRequestHandler extends AbstractConfiguratedPathRequestHandler {

	private static final long serialVersionUID = 7154339775034959876L;

	@Autowired
	private CampgamesService campgamesService;

	@Override
	protected Path getPath(String fileName) throws FileNotFoundException {
		if (!fileName.matches("[0-9]+/[^/]+"))
			throw new FileNotFoundException();
		String[] chunks = fileName.split("/");
		Long id = Long.parseLong(chunks[0]);
		String name = chunks[1];
		return campgamesService.getCampgameImagesFilePath(id, name);
	}

	@Override
	protected String getMimeType(Path file) {
		return super.getMimeType(file);
	}

}
