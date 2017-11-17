package cz.gattserver.grass3.model.dao;

import java.util.List;

import cz.gattserver.grass3.model.dto.ContentTagCountTO;

public interface ContentTagRepositoryCustom {

	int countContentTagContents(Long id);

	ContentTagCountTO findTagContentNodesLowestCount();

	ContentTagCountTO findTagContentNodesHighestCount();
	
	List<ContentTagCountTO> countContentTagsContents();

}
