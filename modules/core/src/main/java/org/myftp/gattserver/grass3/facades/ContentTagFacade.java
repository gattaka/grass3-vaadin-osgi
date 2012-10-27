package org.myftp.gattserver.grass3.facades;

import java.util.List;

import org.myftp.gattserver.grass3.model.dao.ContentTagDAO;
import org.myftp.gattserver.grass3.model.domain.ContentTag;
import org.myftp.gattserver.grass3.model.dto.ContentTagDTO;
import org.myftp.gattserver.grass3.util.Mapper;

public enum ContentTagFacade {

	INSTANCE;

	private Mapper mapper = Mapper.INSTANCE;

	public List<ContentTagDTO> getAllContentTags() {
		ContentTagDAO dao = new ContentTagDAO();

		List<ContentTag> contentTags = dao.findAll();
		if (contentTags == null)
			return null;
		List<ContentTagDTO> contentTagDTOs = mapper
				.mapContentTagCollection(contentTags);

		dao.closeSession();
		return contentTagDTOs;
	}

}
