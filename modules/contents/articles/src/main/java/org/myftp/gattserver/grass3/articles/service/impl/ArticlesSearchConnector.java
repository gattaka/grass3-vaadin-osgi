package org.myftp.gattserver.grass3.articles.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.myftp.gattserver.grass3.articles.dto.ArticleDTO;
import org.myftp.gattserver.grass3.articles.facade.ArticleFacade;
import org.myftp.gattserver.grass3.articles.windows.ArticlesViewerWindow;
import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;
import org.myftp.gattserver.grass3.search.service.ISearchConnector;
import org.myftp.gattserver.grass3.search.service.ISearchField;
import org.myftp.gattserver.grass3.search.service.SearchEntity;
import org.myftp.gattserver.grass3.security.CoreACL;
import org.myftp.gattserver.grass3.util.URLIdentifierUtils;

public class ArticlesSearchConnector implements ISearchConnector {

	private ArticleFacade articleFacade = ArticleFacade.INSTANCE;

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
						ArticlesViewerWindow.class, suffix);

				searchEntity.addField(ArticleSearchField.NAME, article
						.getContentNode().getName(), true);
				searchEntity.addField(ArticleSearchField.CONTENT,
						article.getOutputHTML(), true);

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
