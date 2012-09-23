package org.myftp.gattserver.grass3.model.dao;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.myftp.gattserver.grass3.model.AbstractDAO;
import org.myftp.gattserver.grass3.model.domain.Node;

public class NodeDAO extends AbstractDAO<Node> {

	public NodeDAO() {
		super(Node.class);
	}

	public List<Node> getRoots() {
		return findByRestriction(Restrictions.isNull("parentID"), null, null);
	}
}
