package cz.gattserver.grass3.articles.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.dto.ArticleDTO;
import cz.gattserver.grass3.articles.facade.IArticleFacade;
import cz.gattserver.grass3.model.dto.UserInfoDTO;
import cz.gattserver.grass3.pages.factories.template.IPageFactory;
import cz.gattserver.grass3.search.service.ISearchConnector;
import cz.gattserver.grass3.search.service.ISearchField;
import cz.gattserver.grass3.search.service.SearchEntity;
import cz.gattserver.grass3.security.ICoreACL;
import cz.gattserver.web.common.URLIdentifierUtils;

@Component("articlesSearchConnector")
public class ArticlesSearchConnector implements ISearchConnector {

	@Resource(name = "coreACL")
	private ICoreACL coreACL;

	@Resource(name = "articleFacade")
	private IArticleFacade articleFacade;

	@Resource(name = "articlesViewerPageFactory")
	private IPageFactory articlesViewerPageFactory;

	public List<SearchEntity> getAvailableSearchEntities(UserInfoDTO user) {

		List<SearchEntity> searchEntities = new ArrayList<SearchEntity>();

		List<ArticleDTO> articles = articleFacade.getAllArticlesForSearch();
		for (ArticleDTO article : articles) {

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

	public Enum<? extends ISearchField>[] getSearchFields() {
		return ArticleSearchField.values();
	}

	public String getModuleId() {
		return "Články";
	}

	public String getLinkFieldName() {
		return "link";
	}

}
