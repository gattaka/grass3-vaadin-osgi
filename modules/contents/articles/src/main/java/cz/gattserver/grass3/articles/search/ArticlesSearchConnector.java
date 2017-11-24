package cz.gattserver.grass3.articles.search;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.interfaces.ArticleTO;
import cz.gattserver.grass3.articles.services.ArticleService;
import cz.gattserver.grass3.interfaces.UserInfoTO;
import cz.gattserver.grass3.search.service.SearchConnector;
import cz.gattserver.grass3.search.service.SearchField;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass3.search.service.SearchEntity;
import cz.gattserver.web.common.URLIdentifierUtils;

@Component("articlesSearchConnector")
public class ArticlesSearchConnector implements SearchConnector {

	@Autowired
	private ArticleService articleFacade;

	@Resource(name = "articlesViewerPageFactory")
	private PageFactory articlesViewerPageFactory;

	public List<SearchEntity> getAvailableSearchEntities(UserInfoTO user) {

		List<SearchEntity> searchEntities = new ArrayList<SearchEntity>();

		List<ArticleTO> articles = articleFacade.getAllArticlesForSearch();
		for (ArticleTO article : articles) {

			// TODO
			// if (coreACL.canShowContent(article.getContentNode(), user)) {

			String suffix = URLIdentifierUtils.createURLIdentifier(article.getContentNode().getContentID(),
					article.getContentNode().getName());

			SearchEntity searchEntity = new SearchEntity(articlesViewerPageFactory, suffix);

			searchEntity.addField(ArticleSearchField.NAME, article.getContentNode().getName(), true);
			searchEntity.addField(ArticleSearchField.CONTENT, article.getSearchableOutput(), true);

			searchEntities.add(searchEntity);

			// }
		}

		return searchEntities;

	}

	public Enum<? extends SearchField>[] getSearchFields() {
		return ArticleSearchField.values();
	}

	public String getModuleId() {
		return "Články";
	}

	public String getLinkFieldName() {
		return "link";
	}

}
