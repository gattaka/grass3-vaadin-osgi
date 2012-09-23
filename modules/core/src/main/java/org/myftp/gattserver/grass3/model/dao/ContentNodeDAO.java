package org.myftp.gattserver.grass3.model.dao;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.myftp.gattserver.grass3.model.AbstractDAO;
import org.myftp.gattserver.grass3.model.domain.ContentNode;

public class ContentNodeDAO extends AbstractDAO<ContentNode> {

	public ContentNodeDAO() {
		super(ContentNode.class);
	}

	private List<ContentNode> findRecentBy(String byWhat, Integer maxResults,
			Criterion restriction) {
		return findByRestriction(
				Restrictions.and(Restrictions.isNotNull(byWhat), restriction),
				Order.desc(byWhat), maxResults);
	}

	public List<ContentNode> findRecentAdd(int maxResults, Long nodeId) {
		return findRecentBy("creationDate", maxResults, Restrictions.and(
				Restrictions.isNotNull("creationDate"),
				Restrictions.eq("parentID", nodeId)));
	}

	public List<ContentNode> findRecentAdd(int maxResults) {
		return findRecentBy("creationDate", maxResults,
				Restrictions.isNotNull("creationDate"));
	}

	public List<ContentNode> findContentByUserId(Long userId) {
		return findRecentBy("creationDate", null,
				Restrictions.eq("author.id", userId));
	}

	public List<ContentNode> findRecentEdit(int maxResults) {
		return findRecentBy("lastModificationDate", maxResults,
				Restrictions.isNotNull("lastModificationDate"));
	}

	public List<ContentNode> findRecentEdit(int maxResults, Long nodeId) {
		return findRecentBy("lastModificationDate", maxResults,
				Restrictions.and(
						Restrictions.isNotNull("lastModificationDate"),
						Restrictions.eq("parentID", nodeId)));
	}

}
