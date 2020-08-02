package cz.gattserver.grass3.articles;

import java.io.FileNotFoundException;
import java.nio.file.Path;

import javax.servlet.annotation.WebServlet;

import org.springframework.beans.factory.annotation.Autowired;

import cz.gattserver.grass3.articles.config.ArticlesConfiguration;
import cz.gattserver.grass3.articles.services.ArticleService;
import cz.gattserver.grass3.exception.GrassPageException;
import cz.gattserver.grass3.server.AbstractConfiguratedPathRequestHandler;
import cz.gattserver.web.common.spring.SpringContextHelper;

@WebServlet(urlPatterns = "/" + ArticlesConfiguration.ATTACHMENTS_PATH + "/*")
public class AttachmentsRequestHandler extends AbstractConfiguratedPathRequestHandler {

	private static final long serialVersionUID = 7154339775034959876L;

	@Autowired
	private ArticleService articleService;

	public AttachmentsRequestHandler() {
		SpringContextHelper.inject(this);
	}

	@Override
	protected Path getPath(String fileName) throws FileNotFoundException {
		if (!fileName.matches("/[0-9]+/[^/]+"))
			throw new GrassPageException(400);
		String[] chunks = fileName.split("/");
		String id = chunks[1];
		String name = chunks[2];
		return articleService.getAttachmentFilePath(id, name);
	}

	@Override
	protected String getMimeType(Path file) {
		String type = super.getMimeType(file);
		return type + "; charset=utf-8";
	}

}
