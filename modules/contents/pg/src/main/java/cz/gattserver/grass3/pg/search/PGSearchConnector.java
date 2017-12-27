package cz.gattserver.grass3.pg.search;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.interfaces.UserInfoTO;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryTO;
import cz.gattserver.grass3.pg.service.PGService;
import cz.gattserver.grass3.search.service.SearchConnector;
import cz.gattserver.grass3.search.service.SearchField;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.web.common.server.URLIdentifierUtils;
import cz.gattserver.grass3.search.service.SearchEntity;

@Component("pgSearchConnector")
public class PGSearchConnector implements SearchConnector {

	@Autowired
	private PGService photogalleryFacade;

	@Resource(name = "pgViewerPageFactory")
	private PageFactory pgViewerPageFactory;

	public List<SearchEntity> getAvailableSearchEntities(UserInfoTO user) {
		List<SearchEntity> searchEntities = new ArrayList<>();
		List<PhotogalleryTO> photogalleries = photogalleryFacade.getAllPhotogalleriesForSearch();
		for (PhotogalleryTO photogallery : photogalleries) {
			String suffix = URLIdentifierUtils.createURLIdentifier(photogallery.getContentNode().getContentID(),
					photogallery.getContentNode().getName());
			SearchEntity searchEntity = new SearchEntity(pgViewerPageFactory, suffix);
			searchEntity.addField(PGSearchField.NAME, photogallery.getContentNode().getName(), true);
			searchEntities.add(searchEntity);
		}
		return searchEntities;
	}

	public Enum<? extends SearchField>[] getSearchFields() {
		return PGSearchField.values();
	}

	public String getModuleId() {
		return "Fotogalerie";
	}

	public String getLinkFieldName() {
		return "link";
	}

}
