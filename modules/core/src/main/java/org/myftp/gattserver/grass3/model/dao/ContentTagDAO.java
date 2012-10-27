package org.myftp.gattserver.grass3.model.dao;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.myftp.gattserver.grass3.model.AbstractDAO;
import org.myftp.gattserver.grass3.model.domain.ContentTag;

public class ContentTagDAO extends AbstractDAO<ContentTag> {

	public ContentTagDAO() {
		super(ContentTag.class);
	}

	/**
	 * Získá tag dle názvu
	 */
	public ContentTag findContentTagByName(String name) {
		List<ContentTag> tags = findByRestriction(Restrictions.eq("name", name),
				null, null);
		if (tags == null)
			return null;
		if (tags.isEmpty())
			return null;
		return tags.get(0);
	}

}
