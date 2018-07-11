package cz.gattserver.grass3.hw;

import java.io.FileNotFoundException;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.hw.service.HWService;
import cz.gattserver.grass3.server.AbstractConfiguratedPathRequestHandler;

@Component("hwRequestHandler")
public class HWRequestHandler extends AbstractConfiguratedPathRequestHandler {

	private static final long serialVersionUID = 7154339775034959876L;

	@Autowired
	private HWService hwService;

	public HWRequestHandler() {
		super(HWConfiguration.HW_PATH);
	}

	@Override
	protected Path getPath(String fileName) throws FileNotFoundException {
		if (!fileName.matches("[0-9]+/(img|doc)/[^/]+"))
			throw new FileNotFoundException();
		String[] chunks = fileName.split("/");
		Long id = Long.parseLong(chunks[0]);
		String type = chunks[1];
		String name = chunks[2];
		if ("img".equals(type)) {
			return hwService.getHWItemImagesFilePath(id, name);
		}
		return hwService.getHWItemDocumentsFilePath(id, name);
	}

	@Override
	protected String getMimeType(Path file) {
		String type = super.getMimeType(file);
		return type + "; charset=utf-8";
	}

}
