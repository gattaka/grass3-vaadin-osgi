package org.myftp.gattserver.grass3.model.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.myftp.gattserver.grass3.model.AbstractDAO;
import org.myftp.gattserver.grass3.model.domain.ContentNode;
import org.myftp.gattserver.grass3.model.domain.Node;
import org.myftp.gattserver.grass3.model.domain.User;
import org.springframework.stereotype.Component;

@Component("contentNodeDAO")
public class ContentNodeDAO extends AbstractDAO<ContentNode> {

	public ContentNodeDAO() {
		super(ContentNode.class);
	}

	private List<ContentNode> findRecentBy(String byWhat, Integer maxResults,
			Criterion restriction) {
		return findByRestriction(
				Restrictions.and(Restrictions.isNotNull(byWhat), restriction),
				null, maxResults);
	}

	// public List<ContentNode> findRecentAdded(int maxResults) {
	// return findRecentBy("creationDate", maxResults,
	// null);
	// }

	@SuppressWarnings("unchecked")
	public List<ContentNode> findRecentAdded(Integer maxResults) {
		Transaction tx = null;
		List<ContentNode> list = null;
		openSession();
		try {
			tx = session.beginTransaction();
			Criteria criteria = session.createCriteria(entityClass);
			criteria.addOrder(Order.desc("creationDate"));
			list = (List<ContentNode>) criteria.setResultTransformer(
					Criteria.DISTINCT_ROOT_ENTITY).list();
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (tx != null) {
				tx.rollback();
			}
			closeSession();
			return null;
		}

		/**
		 * Tohle není úplně košér, protože to omezuje výsledky programově až
		 * poté, co jsou všechny vybrány z DB - je to bohužel vlastnost
		 * hibernate, která způsobuje že během outer joinu (collections apod. to
		 * způsobí) se ponechají identické řádky. Max results tady taky
		 * nepomůže, protože DISTINCT_ROOT_ENTITY se aplikuje až na výsledný
		 * omezený počet řádků, což by mělo být naopak.
		 */
		if (maxResults != null) {
			int limit = list.size() <= maxResults ? list.size() : maxResults;
			return list.subList(0, limit > 0 ? limit : 1);
		} else {
			return list;
		}
	}

	public List<ContentNode> findContentByUserId(Long userId) {
		return findRecentBy("creationDate", null,
				Restrictions.eq("author.id", userId));
	}

	public List<ContentNode> findRecentEdited(int maxResults) {
		return findRecentBy("lastModificationDate", maxResults,
				Restrictions.isNotNull("lastModificationDate"));
	}

	public boolean save(ContentNode contentNode, Long parentId, Long userId) {
		Transaction tx = null;
		openSession();
		try {
			tx = session.beginTransaction();

			Node parent = (Node) session.load(Node.class, parentId);
			if (parent == null) {
				tx.rollback();
				return false;
			}
			contentNode.setParent(parent);

			User user = (User) session.load(User.class, userId);
			if (user == null) {
				tx.rollback();
				return false;
			}
			contentNode.setAuthor(user);

			Long id = (Long) session.save(contentNode);
			contentNode.setId(id);

			if (id == null) {
				tx.rollback();
				return false;
			}

			parent.getContentNodes().add(contentNode);
			session.merge(parent);

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
			ContentNode contentNode = findByIdAndCast(entityClass, nodeId);

			Node node = contentNode.getParent();
			node.getContentNodes().remove(contentNode);
			session.merge(node);

			session.delete(contentNode);
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
