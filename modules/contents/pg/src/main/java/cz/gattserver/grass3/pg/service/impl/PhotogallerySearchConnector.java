package cz.gattserver.grass3.pg.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.model.dto.UserInfoDTO;
import cz.gattserver.grass3.pages.factories.template.IPageFactory;
import cz.gattserver.grass3.pg.dto.PhotogalleryDTO;
import cz.gattserver.grass3.pg.facade.IPhotogalleryFacade;
import cz.gattserver.grass3.search.service.ISearchConnector;
import cz.gattserver.grass3.search.service.ISearchField;
import cz.gattserver.grass3.search.service.SearchEntity;
import cz.gattserver.grass3.security.ICoreACL;
import cz.gattserver.web.common.URLIdentifierUtils;

@Component("photogalleriesSearchConnector")
public class PhotogallerySearchConnector implements ISearchConnector {

	@Resource(name = "coreACL")
	private ICoreACL coreACL;

	@Resource(name = "photogalleryFacade")
	private IPhotogalleryFacade photogalleryFacade;

	@Resource(name = "photogalleryViewerPageFactory")
	private IPageFactory photogalleryViewerPageFactory;

	public List<SearchEntity> getAvailableSearchEntities(UserInfoDTO user) {

		List<SearchEntity> searchEntities = new ArrayList<SearchEntity>();

		List<PhotogalleryDTO> photogalleries = photogalleryFacade
				.getAllPhotogalleriesForSearch();
		for (PhotogalleryDTO photogallery : photogalleries) {

			if (coreACL.canShowContent(photogallery.getContentNode(), user)) {

				String suffix = URLIdentifierUtils.createURLIdentifier(
						photogallery.getContentNode().getContentID(),
						photogallery.getContentNode().getName());

				SearchEntity searchEntity = new SearchEntity(
						photogalleryViewerPageFactory, suffix);

				searchEntity.addField(PhotogallerySearchField.NAME,
						photogallery.getContentNode().getName(), true);
				// searchEntity.addField(PhotogallerySearchField.CONTENT,
				// photogallery.getSearchableOutput(), true);

				searchEntities.add(searchEntity);

			}
		}

		return searchEntities;

	}

	public Enum<? extends ISearchField>[] getSearchFields() {
		return PhotogallerySearchField.values();
	}

	public String getModuleId() {
		return "Fotogalerie";
	}

	public String getLinkFieldName() {
		return "link";
	}

}
