package org.myftp.gattserver.grass3.model.dao;

import java.util.List;

import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
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

	public List<ContentNode> findRecentAdded(int maxResults, Long nodeId) {
		return findRecentBy("creationDate", maxResults, Restrictions.and(
				Restrictions.isNotNull("creationDate"),
				Restrictions.eq("parentID", nodeId)));
	}

	public List<ContentNode> findRecentAdded(int maxResults) {
		return findRecentBy("creationDate", maxResults,
				Restrictions.isNotNull("creationDate"));
	}

	public List<ContentNode> findContentByUserId(Long userId) {
		return findRecentBy("creationDate", null,
				Restrictions.eq("author.id", userId));
	}

	public List<ContentNode> findRecentEdited(int maxResults) {
		return findRecentBy("lastModificationDate", maxResults,
				Restrictions.isNotNull("lastModificationDate"));
	}

	public List<ContentNode> findRecentEdited(int maxResults, Long nodeId) {
		return findRecentBy("lastModificationDate", maxResults,
				Restrictions.and(
						Restrictions.isNotNull("lastModificationDate"),
						Restrictions.eq("parentID", nodeId)));
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
