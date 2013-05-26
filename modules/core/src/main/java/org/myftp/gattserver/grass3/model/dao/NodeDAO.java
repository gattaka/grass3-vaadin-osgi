package org.myftp.gattserver.grass3.model.dao;

import java.util.List;

import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.myftp.gattserver.grass3.model.AbstractDAO;
import org.myftp.gattserver.grass3.model.domain.Node;
import org.springframework.stereotype.Component;

@Component("nodeDAO")
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

	/**
	 * Přesune kategorii do jiného předka nebo do kořene (nedá mu předka).
	 * 
	 * <p>
	 * Tato metoda provádí celou operaci transkačně, takže se například nestane,
	 * že staré vazby minulého předka zůstanou přerušené pokud se operace
	 * nezdaří až při připojování k novému předkovi apod.
	 * </p>
	 * 
	 * @param nodeId
	 *            identifikátor přesouvané kategorie
	 * @param parentId
	 *            identifikátor cílové kategorie (nového předka) nebo null,
	 *            pokud má být kategorie přesunuta "do kořene"
	 * @return <code>true</code> pokud se přidání zdařilo, jinak
	 *         <code>false</code>
	 */
	public boolean moveNode(Long nodeId, Long parentId) {
		Transaction tx = null;
		openSession();
		try {
			tx = session.beginTransaction();

			Node node = findByIdAndCast(entityClass, nodeId);

			if (node.getParent() != null) {
				node.getParent().getSubNodes().remove(node);
				session.merge(node.getParent());
			}

			if (parentId != null) {
				Node newParent = findByIdAndCast(entityClass, parentId);
				newParent.getSubNodes().add(node);
				session.merge(newParent);

				node.setParent(newParent);
			} else {
				node.setParent(null);
			}

			session.merge(node);

			tx.commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			if (tx != null) {
				tx.rollback();
			}
			return false;
		} finally {
			closeSession();
		}
	}

	public boolean delete(Long nodeId) {
		Transaction tx = null;
		openSession();
		try {
			tx = session.beginTransaction();
			Node node = findByIdAndCast(entityClass, nodeId);
			if (!node.getContentNodes().isEmpty()
					|| !node.getSubNodes().isEmpty()) {
				tx.rollback();
				return false;
			}
			node.getParent().getSubNodes().remove(node);
			session.merge(node.getParent());
			session.delete(node);
			tx.commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			if (tx != null) {
				tx.rollback();
			}
			return false;
		} finally {
			closeSession();
		}
	}

	public boolean createNewNode(Node node, Long parentId) {
		Transaction tx = null;
		openSession();
		try {
			tx = session.beginTransaction();

			Node parent = null;
			if (parentId != null) {
				parent = findByIdAndCast(entityClass, parentId);
				if (parent == null) {
					tx.rollback();
					return false;
				}
				node.setParent(parent);
			} else {
				node.setParent(null);
			}

			Long id = (Long) session.save(node);
			node.setId(id);

			if (id == null) {
				tx.rollback();
				return false;
			}

			if (parent != null) {
				parent.getSubNodes().add(node);
				session.merge(parent);
			}

			tx.commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			if (tx != null) {
				tx.rollback();
			}
			return false;
		} finally {
			closeSession();
		}
	}
}
