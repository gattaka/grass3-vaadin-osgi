package org.myftp.gattserver.grass3.model.dao;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.myftp.gattserver.grass3.model.AbstractDAO;
import org.myftp.gattserver.grass3.model.domain.Node;

public class NodeDAO extends AbstractDAO<Node> {

	public NodeDAO() {
		super(Node.class);
	}

	public List<Node> findRoots() {
		return findByRestriction(Restrictions.isNull("parent"), null, null);
	}

	public List<Node> findNodesByParent(Long parentId) {
		return findByRestriction(Restrictions.eq("parent.id", parentId), null,
				null);
	}
}
