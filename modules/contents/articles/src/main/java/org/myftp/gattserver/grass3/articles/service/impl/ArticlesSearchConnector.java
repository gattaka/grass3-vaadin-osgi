package org.myftp.gattserver.grass3.articles.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.articles.dto.ArticleDTO;
import org.myftp.gattserver.grass3.articles.facade.IArticleFacade;
import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;
import org.myftp.gattserver.grass3.pages.factories.template.IPageFactory;
import org.myftp.gattserver.grass3.search.service.ISearchConnector;
import org.myftp.gattserver.grass3.search.service.ISearchField;
import org.myftp.gattserver.grass3.search.service.SearchEntity;
import org.myftp.gattserver.grass3.security.CoreACL;
import org.myftp.gattserver.grass3.util.URLIdentifierUtils;
import org.springframework.stereotype.Component;

@Component("articlesSearchConnector")
public class ArticlesSearchConnector implements ISearchConnector {

	@Resource(name = "articleFacade")
	private IArticleFacade articleFacade;

	@Resource(name = "articlesViewerPageFactory")
	private IPageFactory articlesViewerPageFactory;

	public List<SearchEntity> getAvailableSearchEntities(UserInfoDTO user) {

		CoreACL acl = CoreACL.get(user);

		List<SearchEntity> searchEntities = new ArrayList<SearchEntity>();

		List<ArticleDTO> articles = articleFacade.getAllArticles();
		for (ArticleDTO article : articles) {

			if (acl.canShowContent(article.getContentNode())) {

				String suffix = URLIdentifierUtils.createURLIdentifier(article
						.getContentNode().getContentID(), article
						.getContentNode().getName());

				SearchEntity searchEntity = new SearchEntity(
						articlesViewerPageFactory, suffix);

				searchEntity.addField(ArticleSearchField.NAME, article
						.getContentNode().getName(), true);
				searchEntity.addField(ArticleSearchField.CONTENT,
						article.getSearchableOutput(), true);

				searchEntities.add(searchEntity);

			}
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
