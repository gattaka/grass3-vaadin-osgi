package cz.gattserver.grass3.rest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cz.gattserver.grass3.articles.interfaces.ArticlePayloadTO;
import cz.gattserver.grass3.articles.services.ArticleService;

@Controller
@RequestMapping("/articles")
public class ArticlesResource {

	private static Logger logger = LoggerFactory.getLogger(CoreResource.class);

	@Autowired
	private ArticleService articleService;

	// http://localhost:8180/web/ws/articles/create
	// http://resttesttest.com/ (pozor na http -- nedá se posílaz na http, pokud
	// je resttesttest spuštěn z https
	// POST http://localhost:8180/web/ws/articles/create
	// text test článku...
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<String> smsImport(@RequestParam(value = "text", required = true) String text)
			throws IllegalStateException, IOException {
		logger.info("smsImport /create volán");
		ArticlePayloadTO payload = new ArticlePayloadTO(
				"SMS Import " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("d.M.yyyy")), text,
				new ArrayList<>(), false, "dummy");
		articleService.saveArticle(payload, 1L, 1L);

		logger.info("smsImport /create dokončen");
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
